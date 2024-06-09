package com.sicaus.patapov.services.activity

import android.app.Activity

/**
 * To be implemented by any resource that is linked to the [Activity]
 */
interface ActivityBound {
    fun onStart(activity: Activity)
    fun onStop(activity: Activity)
}