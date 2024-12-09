package com.sicaus.patapov.ui.screens.configuration.camera

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sicaus.patapov.SiCausApplication
import com.sicaus.patapov.services.camera.AvailableCamera
import kotlinx.coroutines.flow.StateFlow

interface CameraConfigurationViewModel {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as SiCausApplication
                val container = application.container
                CameraConfigurationViewModelImpl(
                    camera = container.camera(),
                    permissionProvider = container.permissionProvider())
            }
        }
    }

    val uiState: StateFlow<CameraConfigurationUiState>
    fun selectCamera(availableCamera: AvailableCamera)
}
