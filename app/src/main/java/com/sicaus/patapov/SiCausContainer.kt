package com.sicaus.patapov

import android.app.Activity
import com.sicaus.patapov.services.camera.Camera
import com.sicaus.patapov.services.camera.CameraImpl
import com.sicaus.patapov.services.activity.ActivityBound
import com.sicaus.patapov.services.broadcast.BroadcastImpl
import com.sicaus.patapov.services.permissions.PermissionProvider
import com.sicaus.patapov.services.permissions.PermissionProviderImpl

/**
 * Container provides IoD access to services and data providers
 */
class SiCausContainer: ActivityBound {
    private var existingCamera: Camera? = null
    private var existingBroadcast: BroadcastImpl? = null
    private var existingPermissionProvider: PermissionProvider? = null

    fun camera(): Camera {
        if (existingCamera == null) {
            existingCamera = CameraImpl()
        }
        return existingCamera as Camera
    }

    fun broadcast(): BroadcastImpl {
        if (existingBroadcast == null) {
            existingBroadcast = BroadcastImpl(camera(), permissionProvider())
        }
        return existingBroadcast as BroadcastImpl
    }

    fun permissionProvider(): PermissionProvider {
        if (existingPermissionProvider == null) {
            existingPermissionProvider = PermissionProviderImpl()
        }
        return existingPermissionProvider as PermissionProvider
    }

    override fun onStart(activity: Activity) {
        camera().onStart(activity)
        broadcast().onStart(activity)
        permissionProvider().onStart(activity)
    }

    override fun onStop(activity: Activity) {
        camera().onStop(activity)
        broadcast().onStop(activity)
        permissionProvider().onStop(activity)
    }
}