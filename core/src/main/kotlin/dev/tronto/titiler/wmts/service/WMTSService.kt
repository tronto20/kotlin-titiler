package dev.tronto.titiler.wmts.service

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.controller.option.boxAll
import dev.tronto.titiler.core.incoming.controller.option.getOrNull
import dev.tronto.titiler.core.incoming.controller.option.plus
import dev.tronto.titiler.core.outgoing.adaptor.gdal.SpatialReferenceCRSFactory
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.document.domain.Document
import dev.tronto.titiler.document.domain.DocumentFormat
import dev.tronto.titiler.document.template.ClasspathFileTemplateResolver
import dev.tronto.titiler.document.template.FormatsExpressionDialect
import dev.tronto.titiler.image.incoming.controller.option.ImageFormatOption
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.incoming.controller.option.RenderOption
import dev.tronto.titiler.tile.incoming.controller.option.TileMatrixSetOption
import dev.tronto.titiler.tile.incoming.controller.option.TileOption
import dev.tronto.titiler.tile.incoming.controller.option.TileScaleOption
import dev.tronto.titiler.tile.incoming.usecase.TileInfoUseCase
import dev.tronto.titiler.tile.outgoing.adaptor.resource.ResourceTileMatrixSetFactory
import dev.tronto.titiler.tile.outgoing.port.TileMatrixSetFactory
import dev.tronto.titiler.tile.service.TileService
import dev.tronto.titiler.wmts.incoming.controller.option.MaxZoomOption
import dev.tronto.titiler.wmts.incoming.controller.option.MinZoomOption
import dev.tronto.titiler.wmts.incoming.controller.option.UseEPSGOption
import dev.tronto.titiler.wmts.incoming.controller.option.WMTSOption
import dev.tronto.titiler.wmts.incoming.usecase.WMTSUseCase
import org.locationtech.jts.geom.CoordinateXY
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode

class WMTSService(
    private val wmtsUriTemplate: TemplateString =
        TemplateString("http://localhost:8080/{tileMatrixSetId}/WMTSCapabilities.xml"),
    private val tilesUriTemplate: TemplateString = TemplateString(
        "http://localhost:8080/tiles/{tileMatrixSetId}/{z}/{x}/{y}@{scale}x.{format}"
    ),
    private val templateEngine: TemplateEngine = TemplateEngine(),
    private val crsFactory: CRSFactory = SpatialReferenceCRSFactory,
    private val tileMatrixSetFactory: TileMatrixSetFactory = ResourceTileMatrixSetFactory(),
    private val tileInfoUseCase: TileInfoUseCase = TileService(tileMatrixSetFactory, crsFactory),
) : WMTSUseCase {
    init {
        templateEngine.setTemplateResolver(
            ClasspathFileTemplateResolver().apply {
                templateMode = TemplateMode.XML
                prefix = "templates/wmts/"
                suffix = ".xml"
            }
        )
        templateEngine.addDialect(FormatsExpressionDialect())
    }

    private suspend fun setDefaultTileOptions(tileOptions: OptionProvider<TileOption>): OptionProvider<TileOption> {
        var newOptions = tileOptions
        val tileMatrixSetOption: TileMatrixSetOption? = tileOptions.getOrNull()
        val tileScaleOption: TileScaleOption? = tileOptions.getOrNull()

        if (tileMatrixSetOption == null) {
            newOptions += TileMatrixSetOption(tileMatrixSetFactory.default().id)
        }
        if (tileScaleOption == null) {
            newOptions += TileScaleOption(1)
        }
        return newOptions
    }

    private suspend fun setDefaultRenderOptions(
        renderOptions: OptionProvider<RenderOption>,
    ): OptionProvider<RenderOption> {
        var newOptions = renderOptions
        val imageFormatOption: ImageFormatOption? = renderOptions.getOrNull()
        if (imageFormatOption == null) {
            newOptions += ImageFormatOption(null)
        }
        return newOptions
    }

    override suspend fun wmts(
        openOptions: OptionProvider<OpenOption>,
        imageOptions: OptionProvider<ImageOption>,
        renderOptions: OptionProvider<RenderOption>,
        tileOptions: OptionProvider<TileOption>,
        wmtsOptions: OptionProvider<WMTSOption>,
    ): Document {
        val renderOptions = setDefaultRenderOptions(renderOptions)
        val tileOptions = setDefaultTileOptions(tileOptions)

        val boxedParameters = mutableMapOf<String, List<String>>()
        boxedParameters.putAll(openOptions.boxAll().mapKeys { it.key.lowercase() })
        boxedParameters.putAll(imageOptions.boxAll().mapKeys { it.key.lowercase() })
        boxedParameters.putAll(renderOptions.boxAll().mapKeys { it.key.lowercase() })
        boxedParameters.putAll(tileOptions.boxAll().mapKeys { it.key.lowercase() })

        if (!boxedParameters.keys.containsAll(wmtsUriTemplate.variables)) {
            throw IllegalStateException(
                "missing variables in wmts uri: ${wmtsUriTemplate.variables - boxedParameters.keys} "
            )
        }
        val wmtsUri = buildTemplate(wmtsUriTemplate, boxedParameters)

        boxedParameters["z"] = listOf("{TileMatrix}")
        boxedParameters["x"] = listOf("{TileCol}")
        boxedParameters["y"] = listOf("{TileRow}")
        if (!boxedParameters.keys.containsAll(tilesUriTemplate.variables)) {
            throw IllegalStateException(
                "missing variables in tiles uri: ${tilesUriTemplate.variables - boxedParameters.keys}"
            )
        }
        val tilesUri = buildTemplate(tilesUriTemplate, boxedParameters)

        val tileMatrixSetOption: TileMatrixSetOption? = tileOptions.getOrNull()
        val tileMatrixSet = tileMatrixSetOption?.tileMatrixSetId?.let { tileMatrixSetFactory.fromId(it) }
            ?: tileMatrixSetFactory.default()
        val tileMatrixSetCrs = tileMatrixSet.crs?.let { crsFactory.create(it) } ?: crsFactory.default()
        val tileMatrixSetGeographicCrs = crsFactory.createGeographicCRS(tileMatrixSetCrs)
        val tileMatrixSetGeographicCrsTransform = crsFactory.transformTo(tileMatrixSetCrs, tileMatrixSetGeographicCrs)
        val wgs84Crs = crsFactory.create("EPSG:4326")
        val tileInfo = tileInfoUseCase.tileInfo(openOptions, tileOptions)
        val imageFormatOption: ImageFormatOption? = renderOptions.getOrNull()
        val mediaType = imageFormatOption?.format?.contentType ?: "image/unknown"

        val minZoomOption: MinZoomOption? = wmtsOptions.getOrNull()
        val maxZoomOption: MaxZoomOption? = wmtsOptions.getOrNull()
        val zoomLevels = (minZoomOption?.minZoom ?: tileInfo.minZoom)..(maxZoomOption?.maxZoom ?: tileInfo.maxZoom)

        val useEPSGOption: UseEPSGOption? = wmtsOptions.getOrNull()

        val upperLeft = tileMatrixSetGeographicCrsTransform.transformTo(
            CoordinateXY(tileInfo.info.bounds[0], tileInfo.info.bounds[1])
        )
        val lowerRight = tileMatrixSetGeographicCrsTransform.transformTo(
            CoordinateXY(tileInfo.info.bounds[2], tileInfo.info.bounds[3])
        )
        val bounds = doubleArrayOf(upperLeft.x, upperLeft.y, lowerRight.x, lowerRight.y)
        val context = WMTSContext(
            tileInfo.info.name,
            wmtsUri,
            listOf(
                WMTSContext.Layer(
                    tileInfo.info.name,
                    tileInfo.info.name,
                    bounds,
                    tilesUri
                )
            ),
            if (tileMatrixSetGeographicCrs == wgs84Crs) {
                "WGS84BoundingBox"
            } else {
                "BoundingBox"
            },
            if (tileMatrixSetGeographicCrs == wgs84Crs) {
                "urn:ogc:def:crs:OGC:2:84"
            } else {
                tileMatrixSetGeographicCrs.uri.toString()
            },
            mediaType,
            WMTSContext.TileMatrixSet(
                tileMatrixSet.id,
                if (useEPSGOption?.useEpsg == true) tileMatrixSetCrs.epsgCode.toString() else tileMatrixSetCrs.input,
                tileMatrixSet.sortedTileMatrices.filter {
                    it.zoomLevel in zoomLevels
                }.map {
                    WMTSContext.TileMatrix(
                        it.id,
                        it.scaleDenominator,
                        it.pointOfOrigin.value,
                        it.tileWidth,
                        it.tileHeight,
                        it.matrixWidth,
                        it.matrixHeight
                    )
                }
            )
        )
        val xml = templateEngine.process("wmts", context)
        return Document(xml, DocumentFormat.XML)
    }

    private fun buildTemplate(templateString: TemplateString, parameters: Map<String, List<String>>): String {
        val baseUri = templateString.putAll(parameters)
        val queryParameters = parameters - templateString.variables.toSet()
        if (queryParameters.isEmpty()) {
            return baseUri
        } else {
            return "$baseUri?" + queryParameters.flatMap { it.value.map { v -> it.key to v } }
                .joinToString(separator = "&") { "${it.first}=${it.second}" }
        }
    }
}

// fun main() {
//    val map = mapOf(
//        "z" to "test"
//    )
//     val template  =TemplateString("http://localhost:8080/{tileMatrixSetId}/WMTSCapabilities.xml")
//    println(template.variables)
//    println(map - template.variables)
// }
