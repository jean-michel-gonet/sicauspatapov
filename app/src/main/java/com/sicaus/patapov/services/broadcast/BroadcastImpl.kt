package com.sicaus.patapov.services.broadcast

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.media.AudioFormat
import android.view.Surface
import com.sicaus.patapov.services.camera.Camera
import com.sicaus.patapov.services.permissions.PermissionProvider
import com.sicaus.patapov.services.permissions.RequiredPermission
import io.github.thibaultbee.streampack.data.AudioConfig
import io.github.thibaultbee.streampack.data.VideoConfig
import io.github.thibaultbee.streampack.ext.srt.data.SrtConnectionDescriptor
import io.github.thibaultbee.streampack.ext.srt.streamers.CameraSrtLiveStreamer

class BroadcastImpl(
    private val camera: Camera,
    private val permissionProvider: PermissionProvider): Broadcast {
    private val srtConnectionDescriptor = SrtConnectionDescriptor(
        host = "192.168.178.40",
        port = 9998
    )

    private var activity: Activity? = null
    private var streamer: CameraSrtLiveStreamer? = null

    override fun onStart(activity: Activity) {
        this.activity = activity;
    }

    override fun onStop(activity: Activity) {
        this.activity = null
    }

    override fun requiredPermissions(): Collection<RequiredPermission> {
        return mutableListOf(
            RequiredPermission(
                Manifest.permission.CAMERA,
                "Camera allows to see from the Point Of View of your vehicle - without it, this application is useless"
            ),
            RequiredPermission(
                Manifest.permission.RECORD_AUDIO,
                "Recording audio allows to hear what's happening around your vehicle."
            )
        )
    }

    @SuppressLint("MissingPermission")
    override suspend fun startPreview(surface: Surface) {
        permissionProvider.verifyPermissions(this)
        val audioConfig = AudioConfig(
            startBitrate = 128000,
            sampleRate = 44100,
            channelConfig = AudioFormat.CHANNEL_IN_STEREO
        )
        val videoConfig = VideoConfig(
            startBitrate = 2000000, // 2 Mb/s
            resolution = camera.describeSelectedCamera().sizeInPixels,
            fps = 15
        )
        streamer = CameraSrtLiveStreamer(context = activity!!.baseContext)
        streamer?.configure(audioConfig, videoConfig)
        streamer?.startPreview(surface, camera.describeSelectedCamera().cameraId)
        streamer?.connect(srtConnectionDescriptor)
        streamer?.startStream()
    }

    override suspend fun startBroadcast() {
    }

    override suspend fun stopBroadcast() {
    }

    override suspend fun stopPreview() {
        streamer?.stopStream()
        streamer?.stopPreview()
    }
}