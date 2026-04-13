package com.helpful.healingnotes.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.helpful.healingnotes.audio.ToneEngine

class ToneViewModelFactory(
    private val engine: ToneEngine,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ToneViewModel(engine, context) as T
    }
}