package io.github.tronto20.titiler

import org.gdal.gdal.gdal

object GdalInit {
    init {
        gdal.AllRegister()
    }
}
