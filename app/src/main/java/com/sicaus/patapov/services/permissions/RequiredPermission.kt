package com.sicaus.patapov.services.permissions

/**
 * Describes one required permission together with the reason why it is needed.
 */
data class RequiredPermission(
    val permission: String,
    val reason: String)