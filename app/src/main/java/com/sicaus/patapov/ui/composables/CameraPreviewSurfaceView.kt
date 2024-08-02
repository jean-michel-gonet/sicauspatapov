package com.sicaus.patapov.ui.composables

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.core.content.ContextCompat
import com.sicaus.patapov.services.camera.SelectedCameraDescription

/**
 * A custom [TextureView], adapted for camera preview.
 */
@SuppressLint("ViewConstructor") // No need of view constructor, as we're using jetpack.
class CameraPreviewSurfaceView(private val cameraDescription: SelectedCameraDescription, context: Context): TextureView(context) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val displayWidth = MeasureSpec.getSize(widthMeasureSpec)
        val displayHeight = MeasureSpec.getSize(heightMeasureSpec)
        Log.i(this.javaClass.simpleName, "Size is: $displayWidth x $displayHeight")

        val cameraOutputWidth = cameraDescription.size.width
        val cameraOutputHeight = cameraDescription.size.height
        Log.i(this.javaClass.simpleName, "Camera output size is: $cameraOutputHeight x $cameraOutputWidth")

        val relativeRotation = computeRelativeRotation()
        Log.i(this.javaClass.simpleName, "Relative rotation is: $rotation")

        if (cameraOutputWidth > 0f && cameraOutputHeight > 0f) {

            /* Scale factor required to scale the preview to its original size on the x-axis. */
            val scaleX =
                if (relativeRotation % 180 == 0) {
                    displayWidth.toFloat() / cameraOutputWidth
                } else {
                    displayWidth.toFloat() / cameraOutputHeight
                }
            Log.i(this.javaClass.simpleName, "ScaleX is: $scaleX")

            /* Scale factor required to scale the preview to its original size on the y-axis. */
            val scaleY =
                if (relativeRotation % 180 == 0) {
                    displayHeight.toFloat() / cameraOutputHeight
                } else {
                    displayHeight.toFloat() / cameraOutputWidth
                }
            Log.i(this.javaClass.simpleName, "ScaleY is: $scaleY")

            /* Scale factor required to fit the preview to the SurfaceView size. */
            val finalScale = scaleX.coerceAtMost(scaleY)
            Log.i(this.javaClass.simpleName, "Final scale is: $finalScale")

            setScaleX(1 / scaleX * finalScale)
            setScaleY(1 / scaleY * finalScale)
        }
        setMeasuredDimension(displayWidth, displayHeight)
    }

    /**
     * Computes rotation required to transform the camera sensor output orientation to the
     * device's current orientation in degrees.
     * @return Relative rotation of the camera sensor output.
     */
    private fun computeRelativeRotation(): Int {
        val surfaceOrientation = surfaceOrientation()
        val cameraOrientation = cameraDescription.orientation;
        // Reverse device orientation for back-facing cameras.
        /*
        val sign = if (characteristics.get(CameraCharacteristics.LENS_FACING) ==
            CameraCharacteristics.LENS_FACING_FRONT
        ) 1 else -1
        */
        val sign = 1

        // Calculate desired orientation relative to camera orientation to make
        // the image upright relative to the device orientation.
        return (cameraOrientation - surfaceOrientation * sign + 360) % 360
    }

    private fun surfaceOrientation(): Int {
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