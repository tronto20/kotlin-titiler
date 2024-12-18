package dev.tronto.titiler.image.exception

import dev.tronto.titiler.core.exception.DataNotExistsException
import dev.tronto.titiler.image.domain.Window

open class ImageOutOfBoundsException(requested: Window, raster: Window) :
    DataNotExistsException(
        "Requested $requested is out of bounds of $raster"
    )
