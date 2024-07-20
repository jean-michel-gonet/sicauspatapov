package com.sicaus.patapov.services.camera

import android.util.Size

/**
 * Describes the currently selected camera.
 */
data class SelectedCameraDescription (
    val cameraId: String,
    val size: Size
) {
    override fun toString(): String {
        return "CameraId: $cameraId - ${size.width}x${size.height} px"
    }
}