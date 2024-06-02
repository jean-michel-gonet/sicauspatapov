package com.sicaus.patapov.services

import android.app.Activity
import android.view.Surface
import androidx.lifecycle.LifecycleOwner
import com.sicaus.patapov.utils.RequiredPermission

/**
 * A simplified access to the camera.
 */
interface Camera: ActivityBound, RequiringPermissions {
    /**
     * Subscribes a surface to the camera stream.
     * Subscription is possible before or after starting the camera.
     * The number of subscriptors is not limited.
     */
    fun subscribe(surface: Surface)

    /**
     * Starts the camera.
     * Before starting the camera, check for [requiredPermissions].
     * Starting the camera requires access to the application's life cycle.
     */
    suspend fun start(activity: Activity)

    /**
     * Stops the camera.
     * It is possible to stop the camera from outside the user interface.
     * If the camera didn't start, then this method does nothing.
     */
    fun stop()
}