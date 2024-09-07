package com.sicaus.patapov.ui.screens.cameracontrol

import android.view.Surface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sicaus.patapov.SiCausApplication
import com.sicaus.patapov.services.camera.Camera
import com.sicaus.patapov.services.camera.CameraException
import com.sicaus.patapov.services.camera.CameraSelectionCriteria
import com.sicaus.patapov.services.camera.SelectedCameraDescription
import com.sicaus.patapov.services.permissions.PermissionProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class CameraControlViewModel(
    private val camera: Camera,
    private val permissionProvider: PermissionProvider
): ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as SiCausApplication
                val container = application.container
                CameraControlViewModel(
                    camera = container.camera(),
                    permissionProvider = container.permissionProvider())
            }
        }
    }

    data class UiState (
        val cameraState: ServiceState = ServiceState.STOPPED,
        val selectedCamera: SelectedCameraDescription? = null,
        val exception: CameraException? = null
    )

    private val _uiState = MutableStateFlow(UiState())

    open val uiState get() = _uiState.asStateFlow()

    init {
        requestPermissionForCamera()
    }

    protected open fun requestPermissionForCamera() {
        _uiState.update {
            it.copy(cameraState = ServiceState.REQUESTING_PERMISSION)
        }
        viewModelScope.launch {
            val permissionsGranted = permissionProvider.verifyPermissions(camera)
            _uiState.update {
                it.copy(
                    cameraState = when (permissionsGranted) {
                        true -> ServiceState.PERMISSION_GRANTED
                        false -> ServiceState.PERMISSION_NOT_GRANTED
                    }
                )
            }
        }
    }

    /**
     * UI calls this method to signal that user wants to start camera.
     * State changes to STARTING_UP, so UI can initialize whatever components are
     * needed to have a [Surface] to provide to [activateCamera].
     */
    open fun startCamera() {
        if (_uiState.value.cameraState == ServiceState.PERMISSION_GRANTED) {
            try {
                _uiState.update {
                    it.copy(
                        cameraState = ServiceState.STARTING_UP,
                        selectedCamera = camera.selectCamera(CameraSelectionCriteria())
                    )
                }
            } catch (e: CameraException) {
                _uiState.update {
                    it.copy(cameraState = ServiceState.ERROR, exception = e)
                }
            }
        }
    }

    /**
     * UI calls this method to provide a [Surface] to subscribe to the camera.
     */
    open fun activateCamera(surface: Surface) {
        if (_uiState.value.cameraState == ServiceState.STARTING_UP) {
            viewModelScope.launch {
                try {
                    camera.subscribe(surface)
                    _uiState.update {
                        it.copy(cameraState = ServiceState.RUNNING)
                    }
                } catch (e: CameraException) {
                    _uiState.update {
                        it.copy(
                            cameraState = ServiceState.ERROR,
                            exception = e)
                    }
                }
            }
        }
    }

    /**
     * UI calls this method to provide a [Surface] to subscribe to the camera.
     */
    open fun deActivateCamera(surface: Surface) {
        if (_uiState.value.cameraState == ServiceState.RUNNING) {
            viewModelScope.launch {
                try {
                    camera.unsubscribe(surface)
                    _uiState.update {
                        it.copy(cameraState = ServiceState.STARTING_UP)
                    }
                } catch (e: CameraException) {
                    _uiState.update {
                        it.copy(
                            cameraState = ServiceState.ERROR,
                            exception = e)
                    }
                }
            }
        }

    }

    open fun stopCamera() {
        if (_uiState.value.cameraState == ServiceState.RUNNING) {
            _uiState.update {
                it.copy(cameraState = ServiceState.STOPPING)
            }
            viewModelScope.launch {
                camera.stop()
                _uiState.update {
                    it.copy(cameraState = ServiceState.PERMISSION_GRANTED)
                }
            }
        }
    }
}