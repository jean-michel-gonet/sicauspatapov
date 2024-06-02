package com.sicaus.patapov.services

import android.view.Surface

interface VideoReporter {
    /**
     * Starts reporting video
     * Initializes the communication with the server.
     * @return a surface to serve as output for a video producer.
     */
    fun start(): Surface

    /**
     * Stops reporting video.
     */
    fun close()
}