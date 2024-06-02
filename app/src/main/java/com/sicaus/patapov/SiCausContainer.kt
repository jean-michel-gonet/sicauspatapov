package com.sicaus.patapov

import android.app.Activity
import com.sicaus.patapov.services.Camera
import com.sicaus.patapov.services.CameraImpl
import com.sicaus.patapov.services.ActivityBound
import com.sicaus.patapov.services.PermissionProvider
import com.sicaus.patapov.services.PermissionProviderImpl

/**
 * Container provides IoD access to services and data providers
 */
class SiCausContainer: ActivityBound {
    private var existingCamera: Camera? = null
    private var existingPermissionProvider: PermissionProvider? = null

    fun camera(): Camera {
        if (existingCamera == null) {
            existingCamera = CameraImpl()
        }
        return existingCamera as Camera
    }

    fun permissionProvider(): PermissionProvider {
        if (existingPermissionProvider == null) {
            existingPermissionProvider = PermissionProviderImpl()
        }
        return existingPermissionProvider as PermissionProvider
    }

    override fun onStart(activity: Activity) {
        camera().onStart(activity)
        permissionProvider().onStart(activity)
    }

    override fun onStop(activity: Activity) {
        camera().onStop(activity)
        permissionProvider().onStop(activity)
    }
}