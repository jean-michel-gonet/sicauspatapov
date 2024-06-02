package com.sicaus.patapov.ui

import android.view.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sicaus.patapov.SiCausApplication
import com.sicaus.patapov.services.Camera
import com.sicaus.patapov.services.PermissionProvider
import com.sicaus.patapov.services.exceptions.CameraException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SiCausViewModel(
    private val camera: Camera,
    private val permissionProvider: PermissionProvider): ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SiCausApplication
                val container = application.container
                SiCausViewModel(camera = container.camera(), permissionProvider = container.permissionProvider())
            }
        }
    }

    data class UiState (
        val cameraState: ServiceState = ServiceState.STOPPED,
        val exception: CameraException? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        requestPermissionForCamera()
    }

    private fun requestPermissionForCamera() {
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

    fun startCamera(surface: Surface) {
        if (uiState.value.cameraState == ServiceState.PERMISSION_GRANTED) {
            _uiState.update {
                it.copy(cameraState = ServiceState.STARTING_UP)
            }
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

    fun stopCamera() {
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