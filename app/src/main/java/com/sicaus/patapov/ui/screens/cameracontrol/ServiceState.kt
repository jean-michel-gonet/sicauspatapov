package com.sicaus.patapov.ui.screens.cameracontrol

/**
 * Describes the possible states of a service.
 */
enum class ServiceState {
    /**
     * The service is stopped.
     */
    STOPPED,

    /**
     * Requesting permissions to start the service.
     */
    REQUESTING_PERMISSION,

    /**
     * Permissions granted.
     * It is possible to start the service.
     */
    PERMISSION_GRANTED,

    /**
     * Permissions not granted.
     * Attempt to start the service will end in a security exception.
     */
    PERMISSION_NOT_GRANTED,

    /**
     * Service is starting up.
     */
    STARTING_UP,

    /**
     * Encountered an error while starting up.
     */
    ERROR,

    /**
     * Service is running.
     */
    RUNNING,

    /**
     * Service is stopping.
     * When service finishes stopping, then its state will be [STOPPED].
     */
    STOPPING;

    /**
     * Indicates that the camera is not running right now, although it could start.
     */
    fun canStart(): Boolean {
        return when(this) {
            STOPPED -> true
            REQUESTING_PERMISSION -> true
            PERMISSION_GRANTED -> true
            PERMISSION_NOT_GRANTED -> false
            STARTING_UP -> false
            ERROR -> false
            RUNNING -> false
            STOPPING -> false
        }
    }

    /**
     * Indicates that the camera is running or about to run.
     */
    fun isRunning(): Boolean {
        return when(this) {
            STOPPED -> false
            REQUESTING_PERMISSION -> false
            PERMISSION_GRANTED -> false
            PERMISSION_NOT_GRANTED -> false
            STARTING_UP -> true
            ERROR -> false
            RUNNING -> true
            STOPPING -> false
        }
    }
}