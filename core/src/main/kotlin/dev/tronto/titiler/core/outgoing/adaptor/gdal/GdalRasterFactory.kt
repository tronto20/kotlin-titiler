package dev.tronto.titiler.core.outgoing.adaptor.gdal

import dev.tronto.titiler.core.domain.ResamplingAlgorithm
import dev.tronto.titiler.core.exception.GdalDatasetOpenFailedException
import dev.tronto.titiler.core.incoming.controller.option.CRSOption
import dev.tronto.titiler.core.incoming.controller.option.EnvOption
import dev.tronto.titiler.core.incoming.controller.option.NoDataOption
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.controller.option.ResamplingOption
import dev.tronto.titiler.core.incoming.controller.option.URIOption
import dev.tronto.titiler.core.outgoing.port.CRS
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.core.outgoing.port.Raster
import dev.tronto.titiler.core.outgoing.port.RasterFactory
import dev.tronto.titiler.image.outgoing.adaptor.gdal.gdalWarpString
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gdal.gdal.Dataset
import org.gdal.gdal.WarpOptions
import org.gdal.gdal.gdal
import org.gdal.gdalconst.gdalconst
import org.gdal.ogr.ogr
import org.gdal.osr.osr
import java.util.*
import kotlin.io.path.toPath

open class GdalRasterFactory(
    private val crsFactory: CRSFactory = SpatialReferenceCRSFactory,
) : RasterFactory {
    companion object {
        init {
            gdal.AllRegister()
            gdal.UseExceptions()
            ogr.UseExceptions()
            osr.UseExceptions()
        }

        protected val logger = KotlinLogging.logger { }
        private val dispatcher =
            Dispatchers.IO.limitedParallelism(
                maxOf(4, Runtime.getRuntime().availableProcessors() * 2 - 1)
            )
    }

    protected open fun createVRT(openOptions: OptionProvider<OpenOption>, raster: GdalRaster): GdalBaseRaster? {
        val crsOption = openOptions.getOrNull<CRSOption>()
        val noData = openOptions.getOrNull<NoDataOption>()?.noData ?: raster.noDataValue

        return if (crsOption != null || noData != null) {
            val resamplingAlgorithm =
                openOptions.getOrNull<ResamplingOption>()?.algorithm ?: ResamplingAlgorithm.NEAREST
            val memoryFile = "/vsimem/${UUID.randomUUID()}.vrt"
            val warpOptions = mutableMapOf(
                "-of" to "VRT",
                "-r" to resamplingAlgorithm.gdalWarpString,
                "-dstalpha" to ""
            )
            if (crsOption != null) {
                val targetCRS: CRS = crsFactory.create(crsOption.crsString)
                warpOptions["-t_srs"] = targetCRS.proj4
            }

            if (noData != null) {
                warpOptions.remove("-dstalpha")
                warpOptions["-srcnodata"] = noData.toString()
                warpOptions["-dstnodata"] = noData.toString()
            }

            if (raster.hasAlphaBand()) {
                /**
                 *  TODO :: mask flag 확인 필요
                 *   any([MaskFlags.alpha in flags for flags in src_dst.mask_flag_enums])
                 */
                warpOptions.remove("-dstalpha")
            }
            val options = warpOptions.flatMap { listOf(it.key, it.value) }.filter { it.isNotBlank() }
            val dataset = gdal.Warp(
                memoryFile,
                arrayOf(raster.dataset),
                WarpOptions(
                    Vector(options)
                )
            )
            GdalMemFileRaster(GdalRaster(dataset, crsFactory), memoryFile)
        } else {
            null
        }
    }

    protected open fun createDataset(openOptions: OptionProvider<OpenOption>): Dataset {
        val uri = openOptions.get<URIOption>().uri
        val path = if (uri.scheme == null) {
            uri.toString()
        } else {
            uri.toPath().toString()
        }
        val dataset: Dataset = try {
            gdal.Open(path, gdalconst.GA_ReadOnly)
        } catch (e: RuntimeException) {
            logger.error(e) { "Failed to open dataset" }
            throw GdalDatasetOpenFailedException(path, e)
        }
        return dataset
    }

    protected open fun <T> applyEnvs(openOptions: OptionProvider<OpenOption>, block: () -> T): T {
        val envOptions = openOptions.list<EnvOption>()
        return try {
            envOptions.forEach {
                gdal.SetThreadLocalConfigOption(it.key, it.value)
            }
            block()
        } finally {
            envOptions.forEach {
                gdal.SetThreadLocalConfigOption(it.key, null)
            }
        }
    }

    suspend fun <T> withGdalRaster(openOptions: OptionProvider<OpenOption>, block: (raster: GdalBaseRaster) -> T): T {
        return withContext(dispatcher) {
            applyEnvs(openOptions) {
                createDataset(openOptions).use { dataset ->
                    GdalRaster(dataset, crsFactory).use { raster ->
                        createVRT(openOptions, raster)?.use {
                            it.use(block)
                        } ?: block(raster)
                    }
                }
            }
        }
    }

    /**
     *  GdalDataset 사용을 하나의 쓰레드에서 진행하도록 강제
     */
    override suspend fun <T> withRaster(openOptions: OptionProvider<OpenOption>, block: (raster: Raster) -> T): T {
        return withGdalRaster(openOptions) {
            block(it)
        }
    }
}
