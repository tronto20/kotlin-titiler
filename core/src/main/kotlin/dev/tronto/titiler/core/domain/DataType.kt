package dev.tronto.titiler.core.domain

enum class DataType(val min: Number, val max: Number) {
    Int8(kotlin.Byte.MIN_VALUE, kotlin.Byte.MAX_VALUE),
    UInt8(0, 255),
    UInt16(UShort.MIN_VALUE.toInt(), UShort.MAX_VALUE.toInt()),
    Int16(Short.MIN_VALUE, Short.MAX_VALUE),
    UInt32(UInt.MIN_VALUE.toLong(), UInt.MAX_VALUE.toLong()),
    Int32(Int.MIN_VALUE, Int.MAX_VALUE),
    UInt64(0, Double.POSITIVE_INFINITY),
    Int64(Long.MIN_VALUE, Long.MAX_VALUE),
    Float32(0, Float.MAX_VALUE),
    Float64(0, Double.MAX_VALUE),
    CInt16(Short.MIN_VALUE, Short.MAX_VALUE),
    CInt32(Int.MIN_VALUE, Int.MAX_VALUE),
    CFloat32(0, Float.MAX_VALUE),
    CFloat64(0, Double.MAX_VALUE),
    ;

    companion object {
        val INT32 = listOf(
            Int8,
            UInt8,
            UInt16,
            Int32,
            CInt16,
            CInt32
        )

        val INT64 = listOf(
            UInt32,
            Int64
        )

        val FLOAT32 = listOf(
            Float32,
            CFloat32
        )

        val FLOAT64 = listOf(
            Float64,
            CFloat64
        )

        val UNSUPPORTED = listOf(
            UInt64
        )
    }
}
