package com.sicaus.patapov.services.camera

import android.util.Size

/**
 * Describes the currently selected camera.
 */
data class SelectedCameraDescription (
    val cameraId: String,
    val sizeInPixels: Size,
    val orientation: Int
) {
    override fun toString(): String {
        return "CameraId: $cameraId - ${sizeInPixels.width}x${sizeInPixels.height} px, $orientation°"
    }
}