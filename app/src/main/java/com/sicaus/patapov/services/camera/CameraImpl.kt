package com.sicaus.patapov.services.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.os.Build
import android.view.Surface
import androidx.annotation.RequiresApi
import com.sicaus.patapov.services.permissions.RequiredPermission
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * A Camera2 implementation of the Camera interface.
 */
class CameraImpl: Camera {
    companion object {
        private val REQUIRED_PERMISSIONS_FOR_CAMERA =
            mutableListOf (
                RequiredPermission(
                    Manifest.permission.CAMERA,
                    "Camera allows to see from the Point Of View of your vehicle - without it, this application is useless"),
                RequiredPermission(
                    Manifest.permission.RECORD_AUDIO,
                    "Recording audio allows to hear what's happening around your vehicle.")
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(
                        RequiredPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        "Writing to external storage enables taking snapshots.")
                    )
                }
            }
    }

    private val executor = Executors.newSingleThreadExecutor()
    private val targets: MutableList<Surface> = mutableListOf()
    private var activity: Activity? = null
    private var captureSession: CameraCaptureSession? = null

    override fun onStart(activity: Activity) {
        this.activity = activity
    }

    override fun onStop(activity: Activity) {
        stop()
        this.activity = null
    }

    override fun requiredPermissions(): Collection<RequiredPermission> {
        return REQUIRED_PERMISSIONS_FOR_CAMERA
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun start(activity: Activity) {
        captureSession?.close()
        if (targets.isNotEmpty()) {
            val device = openCameraDevice(activity)
            captureSession = createCameraCaptureSession(device, targets)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun openCameraDevice(activity: Activity): CameraDevice {
        // Use the context to require the camera manager from the system:
        val cameraManager: CameraManager = activity.baseContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        // Iterate through all cameras to select the front one
        val cameraId = cameraManager.cameraIdList
            .first { cameraId ->
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
                cameraCharacteristics[CameraCharacteristics.LENS_FACING] == CameraMetadata.LENS_FACING_BACK
            }

        // Open selected camera
        // Use suspendCoroutine to transform callback style functions into suspend functions.
        return suspendCoroutine { continuation ->
            try {
                cameraManager.openCamera(
                    cameraId,
                    executor,
                    object : CameraDevice.StateCallback() {
                        override fun onOpened(cameraDevice: CameraDevice) {
                            continuation.resume(cameraDevice)
                        }

                        override fun onDisconnected(cameraDevice: CameraDevice) {
                            continuation.resumeWithException(CannotOpenCameraDisconnectedException())
                        }

                        override fun onError(cameraDevice: CameraDevice, error: Int) {
                            cameraDevice.close()
                            continuation.resumeWithException(CannotOpenCameraErrorException(error))
                        }
                    })
            } catch (e: SecurityException) {
                continuation.resumeWithException(CannotOpenCameraSecurityException(e))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun createCameraCaptureSession(device: CameraDevice, targets: Collection<Surface>): CameraCaptureSession {

        val outputs = targets.map {
            OutputConfiguration(it).apply {
                // The most similar application for a POV is a video call:
                streamUseCase = CameraMetadata.SCALER_AVAILABLE_STREAM_USE_CASES_VIDEO_CALL.toLong()
            }
        }

        return suspendCoroutine { continuation ->
            device.createCaptureSession(SessionConfiguration(
                SessionConfiguration.SESSION_REGULAR, // No need of high FPS.
                outputs,
                executor,
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        continuation.resume(session)
                    }
                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        continuation.resumeWithException(CannotConfigureCameraCaptureSession(device.id))
                    }
                })
            )
        }
    }

    override fun subscribe(surface: Surface) {
        targets.add(surface)
    }

    override fun stop() {
        captureSession?.close()
    }
}