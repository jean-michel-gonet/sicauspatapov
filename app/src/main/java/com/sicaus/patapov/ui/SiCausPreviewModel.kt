package com.sicaus.patapov.ui

import android.view.Surface
import com.sicaus.patapov.services.camera.Camera
import com.sicaus.patapov.services.permissions.PermissionProvider
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SiCausPreviewModel(private val _uiState: UiState): SiCausViewModel(
    camera = mockk<Camera>(),
    permissionProvider = mockk<PermissionProvider>()) {

    override val uiState get() = MutableStateFlow(_uiState).asStateFlow()

    override fun requestPermissionForCamera() {
        // do nothing
    }

    override fun startCamera(surface: Surface) {
        // do nothing
    }

    override fun stopCamera() {
        // do nothing
    }
}