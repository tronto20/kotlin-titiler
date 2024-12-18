package dev.tronto.titiler.core.exception

class UnsupportedCrsStringException(crsString: String) :
    IllegalParameterException(
        "Cannot create SpatialReference. Unsupported crsString : $crsString"
    )
