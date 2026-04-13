package com.helpful.healingnotes.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helpful.healingnotes.audio.ToneEngine
import com.helpful.healingnotes.data.FavoritesRepository
import com.helpful.healingnotes.data.ToneConfig
import com.helpful.healingnotes.data.solfeggioPresets
import kotlinx.coroutines.launch

class ToneViewModel(
    private val engine: ToneEngine,
    context: Context
) : ViewModel() {

    private val repo = FavoritesRepository(context)

    var uiState by mutableStateOf(
        ToneConfig(
            baseFreq = 528.0,
            binauralStart = 10.0,
            binauralEnd = 10.0,
            rampMinutes = 0.0,
            durationSec = null,
            swapEars = false,
            solfeggioOnly = false
        )
    )

    var showPresetSheet by mutableStateOf(false)

    var favorites = mutableStateOf(
        solfeggioPresets.filter { repo.isFavorite(it.freq) }.map { it.freq }.toSet()
    )

    fun toggleFavorite(freq: Double) {
        repo.toggleFavorite(freq)
        favorites.value = favorites.value.toMutableSet().apply {
            if (contains(freq)) remove(freq) else add(freq)
        }
    }

    fun setFrequency(freq: Double) {
        uiState = uiState.copy(baseFreq = freq)
    }

    fun play() {
        viewModelScope.launch {
            // Pass a lambda that returns the LATEST uiState
            engine.play { uiState }
        }
    }

    fun stop() {
        engine.stop()
    }
}