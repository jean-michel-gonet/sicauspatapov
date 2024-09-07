package com.sicaus.patapov.services.camera

import android.view.Surface
import com.sicaus.patapov.services.activity.ActivityBound
import com.sicaus.patapov.services.permissions.RequiringPermissions

/**
 * A simplified access to the camera.
 */
interface Camera: ActivityBound, RequiringPermissions {
    /**
     * Chooses the camera that best fit the selection criteria.
     * Camera description can also be obtained afterwards, by calling [describeSelectedCamera]]
     * @param cameraSelectionCriteria Selection criteria to choose the most appropriate camera.
     * @return Description of the selected camera.
     */
    fun selectCamera(cameraSelectionCriteria: CameraSelectionCriteria): SelectedCameraDescription

    /**
     * Describes the currently selected camera.
     * @return The camera description.
     */
    fun describeSelectedCamera(): SelectedCameraDescription

    /**
     * Subscribes a surface to the camera stream.
     * Subscribing a surface starts or restarts the camera.
     * The number of subscriptors is not limited.
     */
    suspend fun subscribe(surface: Surface)

    /**
     * Unsubscribes a surface from the camera stream.
     * Unsubscribing a surface restarts the camera.
     * If there are no subscriptors, the camera is stopped.
     */
    suspend fun unsubscribe(surface: Surface)

    /**
     * Stops the camera.
     * It is possible to stop the camera from outside the user interface.
     * If the camera didn't start, then this method does nothing.
     */
    fun stop()
}