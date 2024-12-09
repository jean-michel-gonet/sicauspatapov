package com.sicaus.patapov.ui.screens.configuration.camera

import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sicaus.patapov.R
import com.sicaus.patapov.services.camera.AvailableCamera
import com.sicaus.patapov.services.camera.CameraSelectionCriteria

@Composable
fun CameraConfiguration(
    closeCameraConfiguration: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CameraConfigurationViewModel =
        viewModel(factory = CameraConfigurationViewModel.Factory)) {
    Column(modifier){
        val uiState by viewModel.uiState.collectAsState()
        if (uiState is CameraConfigurationUiState.NoCameraDataAvailable) {
            NoCameraData(
                noData = uiState as CameraConfigurationUiState.NoCameraDataAvailable,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f))
        } else {
            CameraSelector(uiState as CameraConfigurationUiState.CameraDataAvailable)
        }
        AcceptOrCancelCameraConfiguration(
            onAccept = closeCameraConfiguration,
            onCancel = closeCameraConfiguration,
            modifier = Modifier
                .fillMaxWidth())
    }
}

@Composable
fun CameraSelector(uiState: CameraConfigurationUiState.CameraDataAvailable) {
    for (availableCamera in uiState.availableCameras) {
        CameraListItem(availableCamera.cameraId)
    }
}

@Composable
fun NoCameraData(
    noData: CameraConfigurationUiState.NoCameraDataAvailable,
    modifier: Modifier = Modifier) {
    when(noData) {
        is CameraConfigurationUiState.NoCameraDataAvailable.PermissionDenied ->
            NoCameraDataPermissionDenied(modifier)
        is CameraConfigurationUiState.NoCameraDataAvailable.WaitingForPermissions ->
            NoCameraDataWaiting(modifier)
        is CameraConfigurationUiState.NoCameraDataAvailable.PermissionGranted ->
            NoCameraDataWaiting(modifier)
    }
}

@Composable
fun NoCameraDataWaiting(modifier: Modifier) {
    Card(modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(10.dp)
                    .aspectRatio(1f),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
fun NoCameraDataPermissionDenied(modifier: Modifier) {
    Card(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Permission denied")
            Box(modifier = Modifier.aspectRatio(1f)) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Camera")
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_forbidden),
                    contentDescription = "Forbidden")
            }
            Text("Go to Android configuration and " +
                    "grant permissions for camera to this " +
                    "application")
        }
    }
}

@Composable
fun AcceptOrCancelCameraConfiguration(
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier) {
    Row(modifier = modifier
        .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Button(
            onClick = onAccept,
            enabled = true) {
            Text(text = stringResource(R.string.button_accept))
        }
        Button(
            onClick = onCancel,
            enabled = true
        ) {
            Text(text = stringResource(R.string.button_cancel))
        }
    }
}

@Preview
@Composable
fun CameraConfigurationWaitingForPermissionsPreview() {
    CameraConfiguration(
        closeCameraConfiguration = {},
        modifier = Modifier,
        CameraConfigurationPreviewModel(
            CameraConfigurationUiState
                .NoCameraDataAvailable
                .WaitingForPermissions()
        ))
}

@Preview
@Composable
fun CameraConfigurationPermissionDeniedPreview() {
    CameraConfiguration(
        closeCameraConfiguration = {},
        modifier = Modifier,
        CameraConfigurationPreviewModel(
            CameraConfigurationUiState
                .NoCameraDataAvailable
                .PermissionDenied()))
}

@Preview
@Composable
fun CameraConfigurationPermissionGrantedPreview() {
    CameraConfiguration(
        closeCameraConfiguration = {},
        modifier = Modifier,
        CameraConfigurationPreviewModel(
            CameraConfigurationUiState
                .NoCameraDataAvailable
                .PermissionGranted()))
}

@Preview
@Composable
fun CameraConfigurationAvailableCamerasPreview() {
    CameraConfiguration(
        closeCameraConfiguration = {},
        modifier = Modifier,
        CameraConfigurationPreviewModel(
            CameraConfigurationUiState
                .CameraDataAvailable(
                    listOf(
                        AvailableCamera(
                            cameraId = "1",
                            facing = CameraSelectionCriteria.Facing.BACK,
                            orientation = 90,
                            minFocalLength = 0.7f,
                            availableOutputSizes = listOf(
                                Size(100, 100),
                                Size(200, 200),
                                Size(300, 300))
                        ),
                        AvailableCamera(
                            cameraId = "2",
                            facing = CameraSelectionCriteria.Facing.BACK,
                            orientation = 90,
                            minFocalLength = 1.7f,
                            availableOutputSizes = listOf(
                                Size(100, 100),
                                Size(200, 200),
                                Size(300, 300))
                        ),
                        AvailableCamera(
                            cameraId = "3",
                            facing = CameraSelectionCriteria.Facing.FRONT,
                            orientation = -90,
                            minFocalLength = 2.7f,
                            availableOutputSizes = listOf(
                                Size(100, 100),
                                Size(200, 200),
                                Size(300, 300))
                        )
                    )
                )
        )
    )
}

@Composable
fun CameraListItem(cameraType: String, modifier: Modifier = Modifier) {
    ListItem(headlineContent = { Text("$cameraType camera") })
}

@Preview
@Composable
fun CameraListItemPreview() {
    CameraListItem("Front", modifier = Modifier)
}

@Composable
fun ChosenCameraConfiguration(modifier: Modifier) {

}

@Preview
@Composable
fun ChosenCameraConfigurationPreview() {
    ChosenCameraConfiguration(modifier = Modifier)
}
