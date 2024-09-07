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
class CameraPreviewSurfaceView(
    private val cameraDescription: SelectedCameraDescription,
    context: Context): SurfaceView(context) {

    /**
     * Sets the size of the inner surface.
     * The camera session picks the camera resolution from the size of the surface, so it
     * is important to match one of the available resolutions.
     */
    init {
        this.holder.setFixedSize(
            cameraDescription.sizeInPixels.width,
            cameraDescription.sizeInPixels.height)
    }

    /**
     * Recalculates the scale required for the camera output to be displayed on the
     * view without distortion.
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
        val cameraOutputAspect =
            cameraOutputWidth.toFloat() / cameraOutputHeight.toFloat()
        val measuredAspect =
            measuredWidth.toFloat() / measuredHeight.toFloat()

        // Surface will be stretched to completely fit the measured area.
        // We want the scale to do the inverse:
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