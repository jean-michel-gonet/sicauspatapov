package com.sicaus.patapov.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sicaus.patapov.R
import com.sicaus.patapov.ui.screens.cameracontrol.CameraControl
import com.sicaus.patapov.ui.screens.configuration.camera.CameraConfiguration
import com.sicaus.patapov.ui.screens.configuration.Configuration


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: NavigationViewModel = viewModel(factory = NavigationViewModel.Factory),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(topBar = { TopBar(onClick = { viewModel.actionConfiguration()}) }) {
            innerPadding ->
        NavHost(
            navController = navController,
            startDestination = uiState.navigationState.name,
            modifier = modifier) {
            composable(route = PataPOVScreens.MAIN.name) {
                CameraControl(modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize())
            }
            composable(route = PataPOVScreens.CONFIGURATION.name) {
                Configuration(
                    onCamera = {viewModel.actionCameraConfiguration()},
                    onNetwork = {viewModel.actionNetworkConfiguration()},
                    modifier = modifier
                        .padding(innerPadding)
                        .fillMaxSize())
            }
            composable(route = PataPOVScreens.CAMERA_CONFIGURATION.name) {
                CameraConfiguration(
                    closeCameraConfiguration = { viewModel.actionCloseCameraConfiguration() },
                    modifier = modifier
                        .padding(innerPadding)
                        .fillMaxSize())
            }
            composable(route = PataPOVScreens.NETWORK_CONFIGURATION.name) {
                // TODO: Place the network configuration here:
                CameraConfiguration(
                    closeCameraConfiguration = { viewModel.actionCloseCameraConfiguration() },
                    modifier = modifier
                        .padding(innerPadding)
                        .fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.application_name))
        },
        actions = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.button_settings)
                )
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.inversePrimary
        )
    )
}


