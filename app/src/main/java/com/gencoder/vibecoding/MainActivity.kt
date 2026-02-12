package com.gencoder.vibecoding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gencoder.vibecoding.ui.navigation.GencoderApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GencoderApp(applicationContext)
        }
    }
}
