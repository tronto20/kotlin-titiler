package io.github.tronto20.titiler.util

import org.gdal.gdal.gdal

object GdalUtils {
    private object Init {
        init {
            gdal.AllRegister()
        }
    }

    fun init() {
        Init
    }
}
