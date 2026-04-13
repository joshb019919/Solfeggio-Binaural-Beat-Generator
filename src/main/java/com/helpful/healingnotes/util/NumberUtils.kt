package com.helpful.healingnotes.util

import java.util.Locale
import kotlin.math.abs
import kotlin.math.round

fun formatHz(value: Double): String {
    // If it is Earth's Heartbeat / Schumann Resonance frequency, show in hundredths
    if (abs(value - 7.83) < 0.001) {
        return "7.83"
    }
    
    // Otherwise, for binaural beats, if it's a half-number, show one decimal place
    if (value % 1.0 != 0.0 && value % 0.5 == 0.0) {
        return String.format(Locale.US, "%.1f", value)
    }

    // Default to whole numbers if it's an integer
    if (value % 1.0 == 0.0) {
        return value.toInt().toString()
    }

    // Fallback for any other decimal values (e.g. from manual typing before snapping)
    return String.format(Locale.US, "%.2f", value).trimEnd('0').trimEnd('.')
}

fun snapSolfeggio(value: Double): Double {
    val target = 7.83
    // If we are within a small range of 7.83, snap to it. 
    // Since 8.0 is the next whole number, we use a threshold that makes it feel natural.
    if (abs(value - target) < 0.4) {
        return target
    }
    return round(value)
}

fun snapBinaural(value: Double): Double {
    val target = 7.83
    // Schumann resonance snapping zone
    if (abs(value - target) < 0.2) {
        return target
    }
    // Snap to 0.5 units
    return round(value * 2.0) / 2.0
}

fun snapToStep(value: Double, step: Double = 0.01): Double {
    return round(value / step) * step
}

fun snapToStep(value: Float, step: Float = 0.01f): Float {
    return round(value / step) * step
}
