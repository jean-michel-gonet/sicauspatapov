package com.sicaus.patapov.ui.screens.cameracontrol

import android.view.Surface
import com.sicaus.patapov.services.broadcast.Broadcast
import com.sicaus.patapov.services.camera.Camera
import com.sicaus.patapov.services.permissions.PermissionProvider
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Extension of [CameraControlViewModel] to be used with Preview.
 * All methods are overridden to do nothing, and state is fixed and directly
 * provided in the constructor.
 */
class CameraControlPreviewModel(private val _uiState: UiState): CameraControlViewModel(
    camera = mockk<Camera>(),
    broadcastService = mockk<Broadcast>(),
    permissionProvider = mockk<PermissionProvider>()
) {

    override val uiState get() = MutableStateFlow(_uiState).asStateFlow()

    override fun requestPermissionForCamera() {
        // do nothing
    }

    override fun activateCamera(surface: Surface) {
        // do nothing
    }

    override fun deActivateCamera(surface: Surface) {
        // do nothing
    }

    override fun stopCamera() {
        // do nothing
    }
}