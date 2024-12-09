package com.sicaus.patapov.ui.screens.configuration.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sicaus.patapov.services.camera.AvailableCamera
import com.sicaus.patapov.services.camera.Camera
import com.sicaus.patapov.services.permissions.PermissionProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class CameraConfigurationViewModelImpl(
    val camera: Camera,
    val permissionProvider: PermissionProvider): ViewModel(), CameraConfigurationViewModel {

    private val _uiState = MutableStateFlow<CameraConfigurationUiState>(CameraConfigurationUiState.NoCameraDataAvailable.WaitingForPermissions())

    override val uiState get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val permissions = permissionProvider.verifyPermissions(camera)
            if (permissions) {
                _uiState.update {
                    CameraConfigurationUiState.NoCameraDataAvailable.PermissionGranted()
                }
            } else {
                _uiState.update {
                    CameraConfigurationUiState.NoCameraDataAvailable.PermissionDenied()
                }
            }

            if (permissions) {
                val availableCameras = camera.availableCameras.last()
                _uiState.update {
                    CameraConfigurationUiState.CameraDataAvailable(availableCameras)
                }
            } else {
                _uiState.update {
                    CameraConfigurationUiState.NoCameraDataAvailable.PermissionDenied()
                }
            }
        }
    }

    override fun selectCamera(availableCamera: AvailableCamera) {
        val s = _uiState.value
        if (s is CameraConfigurationUiState.CameraDataAvailable) {
            _uiState.update {
                CameraConfigurationUiState.CameraSelected(availableCamera, s)
            }
        }
    }
}