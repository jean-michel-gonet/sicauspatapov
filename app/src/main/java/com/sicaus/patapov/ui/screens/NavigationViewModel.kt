package com.sicaus.patapov.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class PataPOVScreens {
    MAIN,
    CONFIGURATION,
    CAMERA_CONFIGURATION,
    NETWORK_CONFIGURATION
}

class NavigationViewModel: ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                NavigationViewModel()
            }
        }
    }

    data class UiState (
        val navigationState: PataPOVScreens = PataPOVScreens.MAIN
    )

    private val _uiState = MutableStateFlow(UiState())

    val uiState get() = _uiState.asStateFlow()

    fun actionConfiguration() {
        _uiState.update {
            if (_uiState.value.navigationState == PataPOVScreens.MAIN) {
                it.copy(navigationState = PataPOVScreens.CONFIGURATION)
            } else {
                it.copy(navigationState = PataPOVScreens.MAIN)
            }
        }
    }

    fun actionNetworkConfiguration() {
        _uiState.update {
            it.copy(navigationState = PataPOVScreens.NETWORK_CONFIGURATION)
        }
    }

    fun actionCloseNetworkConfiguration() {
        _uiState.update {
            it.copy(navigationState = PataPOVScreens.CONFIGURATION)
        }
    }

    fun actionCameraConfiguration() {
        _uiState.update {
            it.copy(navigationState = PataPOVScreens.NETWORK_CONFIGURATION)
        }
    }

    fun actionCloseCameraConfiguration() {
        _uiState.update {
            it.copy(navigationState = PataPOVScreens.CONFIGURATION)
        }
    }
}