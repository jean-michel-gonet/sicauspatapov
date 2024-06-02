package com.sicaus.patapov

import android.app.Application

/**
 * Application context gives access to the container for all other services.
 */
class SiCausApplication: Application() {
    val container: SiCausContainer = SiCausContainer()
}