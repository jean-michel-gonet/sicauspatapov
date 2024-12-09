package com.sicaus.patapov.ui.screens.configuration.camera

import com.sicaus.patapov.services.camera.AvailableCamera

sealed interface CameraConfigurationUiState {
    sealed interface NoCameraDataAvailable: CameraConfigurationUiState {
        open class WaitingForPermissions: NoCameraDataAvailable
        open class PermissionDenied: NoCameraDataAvailable
        open class PermissionGranted: NoCameraDataAvailable
    }
    open class CameraDataAvailable(
        val availableCameras: List<AvailableCamera>)
        : CameraConfigurationUiState
    open class CameraSelected(
        val selected: AvailableCamera,
        cameraDataAvailable: CameraDataAvailable)
        : CameraDataAvailable(cameraDataAvailable.availableCameras)
}
