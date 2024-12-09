package com.sicaus.patapov.ui.screens.configuration.camera

import com.sicaus.patapov.services.camera.AvailableCamera
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraConfigurationPreviewModel(private val _uiState: CameraConfigurationUiState): CameraConfigurationViewModel {
    override val uiState get() = MutableStateFlow(_uiState).asStateFlow()

    override fun selectCamera(availableCamera: AvailableCamera) {
        // Nothing to do.
    }
}