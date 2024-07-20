package com.sicaus.patapov.services.camera

import android.hardware.camera2.CameraCharacteristics
import android.util.Size
import kotlin.math.max
import kotlin.math.min

/**
 * To select camera by its characteristics.
 */
data class CameraSelectionCriteria(
    val facing: Facing = Facing.BACK,
    val focalLength: FocalLength = FocalLength.MEDIUM,
    val size: SizeLimit = SizeLimit(Size(1024, 768))) {

    enum class Facing(private val id: Int) {
        FRONT(CameraCharacteristics.LENS_FACING_FRONT),
        BACK(CameraCharacteristics.LENS_FACING_BACK),
        EXTERNAL(CameraCharacteristics.LENS_FACING_EXTERNAL);

        fun matches(xx: Int?): Boolean {
            return xx == id
        }
    }

    enum class MinMax {
        MIN,
        MAX
    }

    enum class FocalLength(val min: Float, val max: Float) {
        LARGE(2.0f, 100.0f),
        MEDIUM(0.5f, 1.990f),
        SMALL(0.0f, 0.499f);

        fun matches(x: FloatArray?):Boolean {
            return x?.any {
                focalLength ->
                focalLength in min..max
            } ?: false
        }

        override fun toString(): String {
            return "$name ($min to $max)"
        }
    }

    class SizeLimit(val size: Size) {
        fun error(otherSize: Size): Int {
            val total = max(size.height, otherSize.height) * max(size.width, otherSize.width)
            val intersection = min(size.height, otherSize.height) * min(size.width, otherSize.width)
            return total - intersection
        }
        override fun toString(): String {
            return "${size.width} x ${size.height} pixels"
        }
    }

    override fun toString(): String {
        return "Facing $facing, focal length $focalLength, and around $size in size."

    }
}
