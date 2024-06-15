package com.sicaus.patapov.ui.screens.cameracontrol

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sicaus.patapov.R
import com.sicaus.patapov.ui.theme.primaryContainerLight

@Composable
fun CameraControl(modifier: Modifier = Modifier) {
    Camera2AndButtons(modifier)
}

@Composable
fun Camera2AndButtons(
    modifier: Modifier = Modifier,
    viewModel: CameraControlViewModel = viewModel(factory = CameraControlViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Camera2Viewer(
            cameraState = uiState.cameraState,
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth())
        BottomButtons(
            cameraState = uiState.cameraState,
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth())
    }
}

@Composable
fun Camera2Viewer(cameraState: ServiceState, modifier: Modifier = Modifier) {
    if (cameraState.canStart()) {
        WaitingCamera(modifier)
    } else {
        NoCamera(modifier)
    }
}

@Composable
fun WaitingCamera(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_camera),
        contentDescription = "Camera")
}
@Composable
fun NoCamera(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.ic_camera),
            contentDescription = "Camera")
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.ic_forbidden),
            contentDescription = "Forbidden")
    }
}

@Composable
fun BottomButtons(cameraState: ServiceState, modifier: Modifier = Modifier) {
    Row(modifier = modifier
        .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Button(
            onClick = { /*TODO*/ },
            enabled = cameraState.canStart()) {
            Text(text = stringResource(R.string.button_start_camera))
        }
        Button(
            onClick = { /*TODO*/ },
            enabled = cameraState.isRunning()) {
            Text(text = stringResource(id = R.string.button_start_broadcast))
        }
        Button(
            onClick = { /*TODO*/ },
            enabled = cameraState == ServiceState.RUNNING
        ) {
            Text(text = stringResource(R.string.button_stop_camera))
        }
    }
}

@Preview
@Composable
fun Camera2AndButtonsPreview() {
    Camera2AndButtons(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryContainerLight),
        viewModel = CameraControlPreviewModel(
            CameraControlViewModel.UiState(
                cameraState = ServiceState.STOPPED))
    )
}
@Preview
@Composable
fun NoCamera2AndButtonsPreview() {
    Camera2AndButtons(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryContainerLight),
        viewModel = CameraControlPreviewModel(
            CameraControlViewModel.UiState(
                cameraState = ServiceState.PERMISSION_NOT_GRANTED))
    )
}