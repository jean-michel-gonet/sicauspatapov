package com.sicaus.patapov.services

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