package com.sicaus.patapov.services.broadcast

import android.view.Surface
import com.sicaus.patapov.services.activity.ActivityBound
import com.sicaus.patapov.services.permissions.RequiringPermissions

interface Broadcast : ActivityBound, RequiringPermissions {
    suspend fun startPreview(surface: Surface)
    suspend fun startBroadcast()
    suspend fun stopBroadcast()
    suspend fun stopPreview()
}