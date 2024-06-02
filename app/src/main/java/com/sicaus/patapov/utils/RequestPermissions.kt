package com.sicaus.patapov.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

/**
 * Describes one required permission together with the reason why it is needed.
 */
data class RequiredPermission(
    val permission: String,
    val reason: String)

/**
 * Checks and request required permissions.
 * The method starts by checking if the permissions are already granted. If not, then
 * it shows the rationale behind the request, and request the permission. Finally,
 * it calls the action with the outcome of the permission request.
 * It is up to the action to acknowledge the outcome, and adapt the user interface accordingly.
 * @param activity The activity requesting the permissions.
 * @param requiredPermissions The list of required permissions, and their rationale.
 * @param actionToPerform What to call after the permissions are checked positively, or requested.
 */
@Composable
fun RequestPermissions(activity: Activity , requiredPermissions: Collection<RequiredPermission>, actionToPerform: @Composable (Boolean) -> Unit) {
    var permissionGranted: Boolean by remember {
        mutableStateOf(checkIfPermissionsGranted(activity.baseContext, requiredPermissions))
    }

    if (!permissionGranted) {
        showRationale(activity, requiredPermissions)

        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) {
            checkedPermissions -> permissionGranted = acknowledgePermissionRequestOutcome(activity, requiredPermissions, checkedPermissions)
        }

        val requiredPermissionNames = requiredPermissions
            .map {
                it.permission
            }
            .toTypedArray()

        SideEffect {
            launcher.launch(requiredPermissionNames)
        }
    }

    // Performs the action with the status of the permissions.
    actionToPerform(permissionGranted)
}

public fun checkIfPermissionsGranted(context: Context, permissions: Collection<RequiredPermission>): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(context, it.permission) == PackageManager.PERMISSION_GRANTED
    }
}

private fun showRationale(activity: Activity, permissions: Collection<RequiredPermission>) {
    permissions.forEach{
        if (activity.shouldShowRequestPermissionRationale(it.permission)) {
            Toast.makeText(activity.baseContext, it.reason, Toast.LENGTH_SHORT).show()
        }
    }
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
