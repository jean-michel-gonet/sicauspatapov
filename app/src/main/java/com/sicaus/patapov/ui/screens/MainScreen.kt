package com.sicaus.patapov.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sicaus.patapov.R
import com.sicaus.patapov.ui.SiCausViewModel

@Composable
fun SiCausMainScreen(modifier: Modifier = Modifier, viewModel: SiCausViewModel = viewModel(factory = SiCausViewModel.Factory)) {
    val uiState by viewModel.uiState.collectAsState()
    if (uiState.cameraState.canStart()) {

    } else {

    }
}

@Preview
@Composable
fun XxPreview() {
    Xx(modifier = Modifier.fillMaxSize())
}

@Composable
fun Xx(modifier: Modifier =  Modifier) {
    Scaffold(topBar = { TopBar() }) {
        innerPadding ->
        CameraCard(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.application_name))
        },
        actions = {
            IconButton(onClick = { /* TODO */ }) {
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

@Composable
fun CameraCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally) {
        WaitingCamera2(modifier = Modifier
            .weight(1.0f)
            .fillMaxWidth())
        BottomButtons(modifier = Modifier
            .height(60.dp)
            .fillMaxWidth())
    }
}

@Composable
fun BottomButtons(modifier: Modifier = Modifier) {
    Row(modifier = modifier
        .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Start Camera")
        }
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Broadcast")
        }
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Stop")
        }
    }
}

@Preview
@Composable
fun Waiting2Preview() {
    WaitingCamera2()
}

@Preview
@Composable
fun NoCamera2Preview() {
    NoCamera2()
}

@Composable
fun WaitingCamera2(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_camera),
        contentDescription = "Camera")
}
@Composable
fun NoCamera2(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.ic_camera),
            contentDescription = "Camera")
        Image(
            painter = painterResource(id = R.drawable.ic_forbidden),
            contentDescription = "Forbidden")
    }
}
