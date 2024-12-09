package com.sicaus.patapov.ui.screens.configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sicaus.patapov.R
import com.sicaus.patapov.ui.theme.primaryContainerLight

@Composable
fun Configuration(
    onCamera: () -> Unit,
    onNetwork: () -> Unit,
    modifier: Modifier = Modifier) {
    Column(modifier){
        ConfigurationButton(onClick = onCamera,
            painterResource(R.drawable.fa_video_solid),
            stringResource(R.string.configuration_camera),
            stringResource(R.string.configuration_camera_subtitle)
        )
        ConfigurationButton(onClick = onNetwork,
            painterResource(R.drawable.fa_network_wired_solid),
            stringResource(R.string.configuration_network),
            stringResource(R.string.configuration_network_subtitle)
        )
        // TODO: To remove
        ConfigurationButton(onClick = {},
            painterResource(R.drawable.fa_restroom_solid),
            "Other",
            "Don't click here.")
    }
}


@Preview
@Composable
fun ConfigurationPreview() {
    Configuration(
        onCamera = {},
        onNetwork = {},
        modifier = Modifier
            .fillMaxSize()
            .background(primaryContainerLight))
}

@Composable
fun ConfigurationButton(
    onClick: () -> Unit,
    painter: Painter, title:
    String, subTitle: String,
    modifier: Modifier = Modifier) {
    Card(onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(0.dp, 3.dp, 0.dp, 3.dp)) {
        Row(modifier = Modifier
            .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(10.dp, 0.dp, 30.dp, 0.dp)
                    .fillMaxHeight(0.6f))
            Column {
                Text(title,
                    style = MaterialTheme.typography.titleMedium)
                Text(subTitle,
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview
@Composable
fun ConfigurationButtonPreview() {
    ConfigurationButton(onClick = {},
        painterResource(R.drawable.ic_camera),
        "Title",
        "Subtitle, longer, and better")
}