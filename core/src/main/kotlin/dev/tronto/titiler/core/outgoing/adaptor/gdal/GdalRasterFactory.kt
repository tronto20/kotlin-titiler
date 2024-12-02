package dev.tronto.titiler.core.outgoing.adaptor.gdal

import dev.tronto.titiler.core.domain.ResamplingAlgorithm
import dev.tronto.titiler.core.exception.GdalDatasetOpenFailedException
import dev.tronto.titiler.core.incoming.controller.option.CRSOption
import dev.tronto.titiler.core.incoming.controller.option.EnvOption
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.controller.option.ResamplingOption
import dev.tronto.titiler.core.incoming.controller.option.URIOption
import dev.tronto.titiler.core.outgoing.port.CRS
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.core.outgoing.port.Raster
import dev.tronto.titiler.core.outgoing.port.RasterFactory
import dev.tronto.titiler.image.outgoing.adaptor.gdal.gdalConst
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gdal.gdal.Dataset
import org.gdal.gdal.gdal
import org.gdal.gdalconst.gdalconst
import org.gdal.ogr.ogr
import org.gdal.osr.osr
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

    protected open fun createVRT(openOptions: OptionProvider<OpenOption>, from: CRS, dataset: Dataset): Dataset? {
        val crsOption = openOptions.getOrNull<CRSOption>()
        return if (crsOption != null) {
            val resamplingAlgorithm =
                openOptions.getOrNull<ResamplingOption>()?.algorithm ?: ResamplingAlgorithm.NEAREST
            val targetCRS: CRS = crsFactory.create(crsOption.crsString)
            gdal.AutoCreateWarpedVRT(
                dataset,
                from.wkt,
                targetCRS.wkt,
                resamplingAlgorithm.gdalConst
            )
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

    suspend fun <T> withGdalRaster(openOptions: OptionProvider<OpenOption>, block: (raster: GdalRaster) -> T): T {
        return withContext(dispatcher) {
            applyEnvs(openOptions) {
                createDataset(openOptions).use { dataset ->
                    GdalRaster(dataset, crsFactory).use { raster ->
                        createVRT(openOptions, raster.crs, raster.dataset)?.use {
                            GdalRaster(
                                it,
                                crsFactory
                            ).use(block)
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
