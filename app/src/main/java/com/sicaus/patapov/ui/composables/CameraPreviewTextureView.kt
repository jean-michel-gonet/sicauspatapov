package com.sicaus.patapov.ui.composables

import android.annotation.SuppressLint
import android.content.Context
import android.view.Surface
import android.view.SurfaceView
import android.view.TextureView
import androidx.core.content.ContextCompat
import com.sicaus.patapov.services.camera.SelectedCameraDescription

/**
 * A custom [TextureView], adapted for camera preview.
 */
@SuppressLint("ViewConstructor") // No need of view constructor, as we're using jetpack.
class CameraPreviewTextureView(private val cameraDescription: SelectedCameraDescription, context: Context): SurfaceView(context) {
    init {
        this.holder.setFixedSize(
            cameraDescription.sizeInPixels.width,
            cameraDescription.sizeInPixels.height)
    }

    /**
     * Recalculates the scale required for the camera output to be displayed on the view without
     * distortion.
     * [android.view.View] scales the content so as to cover the whole area,
     * not concerned by preserving the aspect ratio.
     * Things to consider in the calculation:
     * - The camera output
     *   - The size of the sensor, in mm - its physical size.
     *   - The size of the output, in pixels
     * - The view actual size
     *   - The view size measured by the [onMeasure] method.
     *   - This method is called when the view need to resize itself - at first display, or
     *   after a rotation, or after any event that affects its size.
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Don't interfere with the normal measure procedure:
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Difference between camera orientation and device orientation:
        val cameraOrientation = cameraDescription.orientation
        val deviceOrientation = deviceOrientation()
        val rotation = cameraOrientation - deviceOrientation

        // Depending on the rotation, the camera width and height are inverted:
        val cameraOutputWidth: Int
        val cameraOutputHeight: Int
        if (rotation % 180 == 0) {
            cameraOutputWidth = cameraDescription.sizeInPixels.width
            cameraOutputHeight = cameraDescription.sizeInPixels.height
        } else {
            cameraOutputWidth = cameraDescription.sizeInPixels.height
            cameraOutputHeight = cameraDescription.sizeInPixels.width
        }

        // Camara output aspect and measured view aspect:
        val cameraOutputAspect = cameraOutputWidth.toFloat() / cameraOutputHeight.toFloat()
        val measuredAspect = measuredWidth.toFloat() / measuredHeight.toFloat()

        // Surface will be stretched to completely fit the measured area. We want the scale
        // to do the inverse:
        val scaleAspect = cameraOutputAspect / measuredAspect

        if (scaleAspect < 1) {
            scaleX = scaleAspect
            scaleY = 1f
        } else {
            scaleX = 1f
            scaleY = 1f / scaleAspect
        }
    }

    /**
     * Obtains the current orientation of the context,
     * and maps it degrees.
     */
    private fun deviceOrientation(): Int {
        val displayRotation = ContextCompat.getDisplayOrDefault(context).rotation
        return when(displayRotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
    }
}