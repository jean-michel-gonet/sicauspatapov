package com.sicaus.patapov

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sicaus.patapov.ui.screens.SiCausMainScreen
import com.sicaus.patapov.ui.theme.SiCausPataPOVTheme


class MainActivity : ComponentActivity() {
    private lateinit var siCausContainer: SiCausContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Add this:
        StrictMode.setVmPolicy(
            VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build())

        try {
            Class.forName("dalvik.system.CloseGuard")
                .getMethod("setEnabled", Boolean::class.javaPrimitiveType)
                .invoke(null, true)
        } catch (e: ReflectiveOperationException) {
            throw RuntimeException(e)
        }

        siCausContainer = (application as SiCausApplication).container

        enableEdgeToEdge()
        setContent {
            SiCausPataPOVTheme {
                SiCausMainScreen()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        siCausContainer.onStart(this)
    }

    override fun onStop() {
        super.onStop()
        siCausContainer.onStop(this)
    }
}
