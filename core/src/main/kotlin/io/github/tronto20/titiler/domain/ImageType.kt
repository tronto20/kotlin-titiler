package io.github.tronto20.titiler.domain

import io.github.tronto20.titiler.GdalInit
import org.gdal.gdal.Driver
import org.gdal.gdal.gdal

enum class ImageType(val driver: String) {
    PNG("PNG"),
    PNGRAW("PNG"),
    NPY("NPY"),
    TIF("GTiff"),
    TIFF("GTiff"),
    JPEG("JPEG"),
    JPG("JPEG"),
    JP2("JP2OpenJPEG"),
    WEBP("WEBP"),
    ;

    fun getDriver(): Driver {
        GdalInit
        return gdal.GetDriverByName(driver)
    }
}
