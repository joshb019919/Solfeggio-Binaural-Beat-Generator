package com.helpful.healingnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.helpful.healingnotes.audio.ToneEngine
import com.helpful.healingnotes.ui.screens.ToneScreen
import com.helpful.healingnotes.ui.theme.SolfeggioBinauralBeatGeneratorTheme
import com.helpful.healingnotes.viewmodel.ToneViewModel
import com.helpful.healingnotes.viewmodel.ToneViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var engine: ToneEngine

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        engine = ToneEngine()

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            SolfeggioBinauralBeatGeneratorTheme {
                val vm: ToneViewModel = viewModel(
                    factory = ToneViewModelFactory(engine, applicationContext)
                )
                ToneScreen(vm, windowSizeClass)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        engine.stop()
    }
}