package dev.tronto.titiler.spring.application.config.adaptor

import dev.tronto.titiler.core.incoming.usecase.InfoUseCase
import dev.tronto.titiler.core.outgoing.adaptor.gdal.GdalRasterFactory
import dev.tronto.titiler.core.outgoing.adaptor.gdal.SpatialReferenceCRSFactory
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.core.outgoing.port.RasterFactory
import dev.tronto.titiler.core.service.CoreService
import dev.tronto.titiler.image.incoming.usecase.ImagePreviewUseCase
import dev.tronto.titiler.image.incoming.usecase.ImageReadUseCase
import dev.tronto.titiler.image.outgoing.adaptor.gdal.GdalReadableRasterFactory
import dev.tronto.titiler.image.outgoing.port.ReadableRasterFactory
import dev.tronto.titiler.image.service.ImageRenderService
import dev.tronto.titiler.image.service.ImageService
import dev.tronto.titiler.stat.service.StatisticsService
import dev.tronto.titiler.tile.incoming.usecase.TileInfoUseCase
import dev.tronto.titiler.tile.outgoing.adaptor.resource.ResourceTileMatrixSetFactory
import dev.tronto.titiler.tile.outgoing.port.TileMatrixSetFactory
import dev.tronto.titiler.tile.service.TileService
import dev.tronto.titiler.wmts.service.TemplateString
import dev.tronto.titiler.wmts.service.WMTSService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.TemplateEngine

@Configuration
class ComponentRegistrar {
    @Bean
    fun webFluxOptionParserAdaptor(): WebFluxOptionParserAdaptor = WebFluxOptionParserAdaptor()

    @Bean
    fun crsFactory(): CRSFactory = SpatialReferenceCRSFactory

    @Bean
    fun rasterFactory(crsFactory: CRSFactory): RasterFactory = GdalRasterFactory(crsFactory)

    @Bean
    fun coreService(rasterFactory: RasterFactory): CoreService = CoreService(rasterFactory)

    @Bean
    fun readableRasterFactory(crsFactory: CRSFactory): GdalReadableRasterFactory = GdalReadableRasterFactory(
        crsFactory,
        GdalRasterFactory(crsFactory)
    )

    @Bean
    fun imageService(crsFactory: CRSFactory, readableRasterFactory: ReadableRasterFactory): ImageService = ImageService(
        crsFactory,
        readableRasterFactory
    )

    @Bean
    fun tileMatrixSetFactory() = ResourceTileMatrixSetFactory()

    @Bean
    fun tileService(
        tileMatrixSetFactory: TileMatrixSetFactory,
        crsFactory: CRSFactory,
        rasterFactory: RasterFactory,
        imageReadUseCase: ImageReadUseCase,
        infoUseCase: InfoUseCase,
    ) = TileService(
        tileMatrixSetFactory,
        crsFactory,
        rasterFactory,
        imageReadUseCase,
        infoUseCase
    )

    @Bean
    fun imageRenderService() = ImageRenderService()

    @Bean
    fun statisticsService(imagePreviewUseCase: ImagePreviewUseCase) = StatisticsService(imagePreviewUseCase)

    @Bean
    fun templateEngine() = TemplateEngine()

    @Bean
    fun wmtsService(
        templateEngine: TemplateEngine,
        crsFactory: CRSFactory,
        tileMatrixSetFactory: TileMatrixSetFactory,
        tileInfoUseCase: TileInfoUseCase,
    ) = WMTSService(
        wmtsUriTemplate = TemplateString("http://localhost:8080/cog/{tileMatrixSetId}/WMTSCapabilities.xml"),
        tilesUriTemplate = TemplateString(
            "http://localhost:8080/cog/tiles/{tileMatrixSetId}/{z}/{x}/{y}@{scale}x.{format}"
        ),
        templateEngine = templateEngine,
        crsFactory = crsFactory,
        tileMatrixSetFactory = tileMatrixSetFactory,
        tileInfoUseCase = tileInfoUseCase
    )
}
