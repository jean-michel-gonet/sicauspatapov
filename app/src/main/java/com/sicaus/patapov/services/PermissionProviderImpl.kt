package com.sicaus.patapov.services

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.sicaus.patapov.utils.RequiredPermission
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PermissionProviderImpl : PermissionProvider {
    private var activity: Activity? = null

    override fun onStart(activity: Activity) {
        this.activity = activity
    }

    override fun onStop(activity: Activity) {
        this.activity = null
    }

    override suspend fun verifyPermissions(requiringPermissions: RequiringPermissions): Boolean {
        // Permissions are linked to the activity. If it is not present, then it is not possible to ask
        if (this.activity == null) {
            return false
        }
        val presentActivity = this.activity as Activity

        // The list of permissions to request
        val permissions = requiringPermissions.requiredPermissions()

        // Maybe permissions are already granted:
        if (checkIfPermissionsGranted(presentActivity, permissions)) {
            return true
        }

        // No? Then let's inform the user
        showRationale(presentActivity, permissions)

        // And now request permissions
        val result = requestPermissions(presentActivity, permissions)
        Log.i(this.javaClass.simpleName, "Permission granted: $result")
        return result
    }

    private fun checkIfPermissionsGranted(presentActivity: Activity, permissions: Collection<RequiredPermission>): Boolean {
        return permissions.all {
                ContextCompat.checkSelfPermission(presentActivity.baseContext, it.permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun showRationale(activity: Activity, permissions: Collection<RequiredPermission>) {
        permissions.forEach{
            if (activity.shouldShowRequestPermissionRationale(it.permission)) {
                Toast.makeText(activity.baseContext, it.reason, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun requestPermissions(activity: Activity, requiredPermissions: Collection<RequiredPermission>): Boolean {
        if (activity !is ActivityResultRegistryOwner) {
            return false
        }
        val activityResultRegistryOwner = activity.activityResultRegistry
        val key = UUID.randomUUID().toString()

        // We'll wait until the activity result comes back:
        var launcher: ActivityResultLauncher<Array<String>>
        val result = suspendCoroutine {
            continuation ->
            launcher = activityResultRegistryOwner.register(key = key, contract = ActivityResultContracts.RequestMultiplePermissions()) {
                checkedPermissions ->
                val permissionsGranted = acknowledgePermissionRequestOutcome(activity, requiredPermissions, checkedPermissions)
                continuation.resume(permissionsGranted)
            }
            launcher.launch(requiredPermissions.map { it.permission }.toTypedArray())
        }
        launcher.unregister()
        Log.i(this.javaClass.simpleName, "Request permissions result: $result")
        return result
    }

    private fun acknowledgePermissionRequestOutcome(activity: Activity, requiredPermissions: Collection<RequiredPermission>, checkedPermissions: Map<String, Boolean>): Boolean {
        // Makes a list with all permissions not granted:
        val missingPermissions = requiredPermissions
            .filter { checkedPermissions[it.permission] != true }

        // If there are missing permissions...
        if (missingPermissions.isNotEmpty()) {
            val message = missingPermissions.joinToString(", ")

            // ... show a toast:
            Toast.makeText(activity.baseContext,
                "Permission request denied: $message",
                Toast.LENGTH_SHORT).show()
        }

        // Updates the permissions flag, so the composable runs again:
        return missingPermissions.isEmpty()
    }
}