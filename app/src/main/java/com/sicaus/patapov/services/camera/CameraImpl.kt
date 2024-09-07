package com.sicaus.patapov.services.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.os.Build
import android.view.Surface
import androidx.annotation.RequiresApi
import com.sicaus.patapov.services.permissions.RequiredPermission
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * A Camera2 implementation of the Camera interface.
 */
class CameraImpl: Camera {
    private val executor = Executors.newSingleThreadExecutor()
    private val targets: MutableList<Surface> = mutableListOf()
    private var activity: Activity? = null
    private var state: InnerState = InnerState.NoCameraSelected()
    private val lock = ReentrantLock()

    override fun requiredPermissions(): Collection<RequiredPermission> {
        return mutableListOf (
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

    /**
     * Structure to hold the inner status of the camera service.
     */
    private sealed interface InnerState {
        /**
         * No camera selected yet.
         */
        open class NoCameraSelected: InnerState

        /**
         * Camera device is selected, but not yet running.
         * This state holds a camera device, plus the description of the available
         * configuration that best fits the selection criteria.
         */
        open class CameraStandby: NoCameraSelected {
            val cameraSelectionCriteria: CameraSelectionCriteria
            val selectedCameraDescription: SelectedCameraDescription

            /**
             * Transits from a higher state.
             * @param state State of origin.
             */
            constructor(state: CameraStandby): super() {
                this.cameraSelectionCriteria = state.cameraSelectionCriteria
                this.selectedCameraDescription = state.selectedCameraDescription
            }

            /**
             * Transits from [NoCameraSelected] state.
             * @param cameraSelectionCriteria Camera selection criteria
             * @param selectedCameraDescription Describes the configuration that best matches
             * the provided selection criteria, in the camera device.
             */
            constructor(
                cameraSelectionCriteria: CameraSelectionCriteria,
                selectedCameraDescription: SelectedCameraDescription,
            ) : super() {

                this.cameraSelectionCriteria = cameraSelectionCriteria
                this.selectedCameraDescription = selectedCameraDescription
            }
        }

        /**
         * Camera is in session, acquiring video.
         */
        open class CameraInSession: CameraStandby {
            val device: CameraDevice
            val captureSession: CameraCaptureSession

            /**
             * Transits from [CameraStandby] state.
             * @param cameraStandby The origin state.
             * @param captureSession The running capture session.
             */
            constructor(cameraStandby: CameraStandby, device: CameraDevice, captureSession: CameraCaptureSession): super(cameraStandby) {
                this.captureSession = captureSession
                this.device = device
            }
        }
    }

    override fun onStart(activity: Activity) {
        this.activity = activity
    }

    override fun onStop(activity: Activity) {
        stop()
        this.activity = null
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun selectCamera(cameraSelectionCriteria: CameraSelectionCriteria): SelectedCameraDescription {
        try {
            lock.lock()
            val newState = doSelectCamera(state, cameraSelectionCriteria)
            state = newState
            return newState.selectedCameraDescription
        } finally {
            lock.unlock()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun doSelectCamera(currentState: InnerState, cameraSelectionCriteria: CameraSelectionCriteria): InnerState.CameraStandby {
        if (currentState is InnerState.CameraInSession) {
            currentState.captureSession.close()
        }

        // If camera is in session, then close the session:
        if (currentState is InnerState.CameraInSession) {
            currentState.device.close()
        }

        // Select a camera:
        val selectedCamera = findCameraAndConfiguration(cameraSelectionCriteria)

        // We're in stand-by, now:
        return InnerState.CameraStandby(
            cameraSelectionCriteria,
            selectedCamera)
    }

    /**
     * Don't assume that your app always runs on a handheld device with one or two cameras.
     * Instead, choose the most appropriate cameras for the app. If you don't need a specific camera,
     * select the first camera that faces the desired direction. If an external camera is connected,
     * you might assume that the user prefers it as the default.
     * [See camera enumeration](https://developer.android.com/media/camera/camera2/camera-enumeration), by Android</a>
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun findCameraAndConfiguration(cameraSelectionCriteria: CameraSelectionCriteria): SelectedCameraDescription {
        // Use the context to require the camera manager from the system:
        // TODO: Add safeguards against activity going null at any moment
        val cameraManager: CameraManager = activity?.baseContext?.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val cameraIdList = cameraManager.cameraIdList
        for (cameraId in cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)

            // Filters out cameras not facing the requested direction:
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (!cameraSelectionCriteria.facing.matches(lensFacing)) {
                continue
            }

            // Filters out cameras not having the expected focal length:
            val availableFocalLengths = characteristics
                .get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
            if (!cameraSelectionCriteria.focalLength.matches(availableFocalLengths)) {
                continue
            }

            // Locates the most appropriate resolution:
            val streamConfigurationMap = characteristics
                .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val outputSizes = streamConfigurationMap?.getOutputSizes(ImageFormat.JPEG)
            val mostAppropriateOutputSize = outputSizes?.minByOrNull {
                    size ->
                cameraSelectionCriteria.size.error(size)
            }
            if (mostAppropriateOutputSize == null) {
                continue
            }

            // Locates the sensor orientation
            val orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

            // Take the first camera that matches:
            return SelectedCameraDescription(
                cameraId = cameraId,
                sizeInPixels = mostAppropriateOutputSize,
                orientation = orientation
            )
        }

        // No matching camera
        throw NoMatchingCameraException(cameraSelectionCriteria)
    }

    override fun describeSelectedCamera(): SelectedCameraDescription {
        return when (val it = state) {
            is InnerState.CameraStandby -> it.selectedCameraDescription
            else -> throw NoCameraSelectedException()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun subscribe(surface: Surface) {
        targets.add(surface)
        restart()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun unsubscribe(surface: Surface) {
        targets.remove(surface)
        restart()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun restart() {
        lock.lock()

        val currentState = state

        if (currentState !is InnerState.CameraStandby) {
            throw NoCameraSelectedException()
        }

        if (currentState is InnerState.CameraInSession) {
            currentState.captureSession.close()
            currentState.device.close()
        }

        if (targets.isEmpty()) {
            state = InnerState.CameraStandby(currentState)
            return
        }

        val device = openCameraDevice(currentState.selectedCameraDescription.cameraId)
        val captureSession = createCameraCaptureSession(device, targets)
        val captureRequestBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        targets.forEach {
            captureRequestBuilder.addTarget(it)
        }
        captureSession.setSingleRepeatingRequest(
            captureRequestBuilder.build(),
            executor,
            object : CameraCaptureSession.CaptureCallback() {
                // TODO: Something useful here?
            })
        state = InnerState.CameraInSession(currentState, device, captureSession)
        lock.unlock()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun openCameraDevice(cameraId: String): CameraDevice {
        // Use the context to require the camera manager from the system:
        val cameraManager: CameraManager = activity?.baseContext?.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        // Open selected camera:
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
            OutputConfiguration(it)
        }

        return suspendCoroutine { continuation ->
             val sessionConfiguration = SessionConfiguration(
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

            device.createCaptureSession(sessionConfiguration)
        }
    }

    override fun stop() {
        lock.lock()
        val currentState = state
        if (currentState is InnerState.CameraInSession) {
            currentState.captureSession.close()
            currentState.device.close()
            state = InnerState.CameraStandby(currentState)
        }
        targets.clear()
        lock.unlock()
    }
}