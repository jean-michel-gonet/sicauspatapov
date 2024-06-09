package com.sicaus.patapov.services.permissions

/**
 * An interface to be implemented by any service requiring permissions.
 */
interface RequiringPermissions {
    /**
     * Lists the required permissions, and the reason why they're needed.
     */
    fun requiredPermissions(): Collection<RequiredPermission>
}