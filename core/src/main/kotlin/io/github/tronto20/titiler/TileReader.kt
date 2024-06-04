package io.github.tronto20.titiler

import io.github.tronto20.titiler.domain.Info
import io.github.tronto20.titiler.domain.Statistics
import io.github.tronto20.titiler.domain.Tile
import io.github.tronto20.titiler.param.ReadRasterParam
import io.github.tronto20.titiler.param.StatisticsParam
import io.github.tronto20.titiler.param.TileParam
import org.locationtech.jts.geom.Envelope
import java.nio.file.Path

interface TileReader {
    suspend fun bounds(srcPath: Path): Envelope

    suspend fun info(srcPath: Path): Info

    suspend fun statistics(
        srcPath: Path,
        readRasterParam: ReadRasterParam,
        statisticsParam: StatisticsParam,
    ): Statistics

    suspend fun tile(
        srcPath: Path,
        readRasterParam: ReadRasterParam,
        tileParam: TileParam,
    ): Tile
}
