package com.sicaus.patapov.services.permissions

import com.sicaus.patapov.services.activity.ActivityBound

/**
 * Provides permission to all services requiring it.
 */
interface PermissionProvider: ActivityBound {
    /**
     * Requests permissions and returns the result.
     * @param requiringPermissions The service requiring the permissions.
     */
    suspend fun verifyPermissions(requiringPermissions: RequiringPermissions): Boolean
}