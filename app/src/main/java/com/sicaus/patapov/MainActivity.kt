package com.sicaus.patapov

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sicaus.patapov.ui.screens.SiCausMainScreen
import com.sicaus.patapov.ui.theme.SiCausPataPOVTheme

class MainActivity : ComponentActivity() {
    lateinit var siCausContainer: SiCausContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
