package com.helpful.healingnotes.data

import android.content.Context
import androidx.core.content.edit

class FavoritesRepository(context: Context) {
    private val prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    fun toggleFavorite(freq: Double) {
        val current = prefs.getBoolean(freq.toString(), false)
        prefs.edit { putBoolean(freq.toString(), !current) }
    }

    fun isFavorite(freq: Double): Boolean {
        return prefs.getBoolean(freq.toString(), false)
    }
}