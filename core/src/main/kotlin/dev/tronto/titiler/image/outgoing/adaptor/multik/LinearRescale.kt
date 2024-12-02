package dev.tronto.titiler.image.outgoing.adaptor.multik

import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.clip
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times

inline fun <T, reified R> linearRescale(
    data: MultiArray<T, D2>,
    from: NumberRange<T>,
    to: NumberRange<R>,
): MultiArray<R, D2> where T : Number, T : Comparable<T>, R : Number, R : Comparable<R> {
    val ratio = to.gap / from.gap
    val clip = data.clip(from.start, from.endInclusive)
    // deepCopy -> view.clip 문제로 인해 deepCopy
    val ratioApplied = (clip - from.start).asType<Double>() * ratio
    return ratioApplied.asType<R>() + to.start
}
