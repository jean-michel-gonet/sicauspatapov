package com.sicaus.patapov.services.camera

import android.hardware.camera2.CameraDevice

/**
 * A family of runtime exceptions for camera.
 */
open class CameraException: RuntimeException {
    protected constructor(m: String): super(m)
    protected constructor(m: String, t: Throwable): super(m, t)
}

open class CannotOpenCameraException: CameraException {
    protected constructor(m: String): super(m)
    protected constructor(m: String, t: Throwable): super(m, t)
}

class CannotOpenCameraErrorException(error: Int): CannotOpenCameraException(
    when(error) {
        CameraDevice.StateCallback.ERROR_CAMERA_IN_USE -> "ERROR_CAMERA_IN_USE"
        CameraDevice.StateCallback.ERROR_MAX_CAMERAS_IN_USE -> "ERROR_MAX_CAMERAS_IN_USE"
        CameraDevice.StateCallback.ERROR_CAMERA_DISABLED -> "ERROR_CAMERA_DISABLED"
        CameraDevice.StateCallback.ERROR_CAMERA_DEVICE -> "ERROR_CAMERA_DEVICE"
        CameraDevice.StateCallback.ERROR_CAMERA_SERVICE -> "ERROR_CAMERA_SERVICE"
        else -> "Unknown camera error: $error"
    })

class CannotOpenCameraSecurityException(e: SecurityException): CannotOpenCameraException(
    "Security exception while opening camera: ${e.message}", e)

class CannotOpenCameraDisconnectedException: CannotOpenCameraException("Camera immediately disconnected")

class CannotConfigureCameraCaptureSession(deviceId: String): CameraException("Cannot configure camera capture session for device $deviceId")