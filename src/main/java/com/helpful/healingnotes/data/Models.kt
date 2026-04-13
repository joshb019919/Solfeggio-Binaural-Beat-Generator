package com.helpful.healingnotes.data

import com.helpful.healingnotes.util.formatHz

data class FrequencyPreset(val freq: Double, val label: String) {
    val displayFreq: String
        get() = formatHz(freq)
}data class BinauralPreset(val name: String, val hz: Double)

data class ToneConfig(
    val baseFreq: Double,
    val binauralStart: Double,
    val binauralEnd: Double,
    val rampMinutes: Double,
    val durationSec: Int?,
    val fadeInSec: Double = 2.0,
    val fadeOutSec: Double = 2.0,
    val swapEars: Boolean,
    val solfeggioOnly: Boolean
)

val solfeggioPresets = listOf(
    FrequencyPreset(7.83,"Earth's Heartbeat"),
    FrequencyPreset(174.00,"Relieve Pain"),
    FrequencyPreset(285.00,"Repair Your Tissues"),
    FrequencyPreset(396.00,"Achieve Your Goals"),
    FrequencyPreset(417.00,"Eliminate Negativity"),
    FrequencyPreset(432.00,"Enhance Mental Clarity"),
    FrequencyPreset(528.00,"Transform"),
    FrequencyPreset(639.00,"Improve Relationships"),
    FrequencyPreset(741.00,"Awaken Your Intuition"),
    FrequencyPreset(852.00,"Embrace Spirit"),
    FrequencyPreset(963.00,"Unlock Divine Consciousness")
)

val binauralPresets = listOf(
    BinauralPreset("Delta", 2.00),
    BinauralPreset("Theta", 6.00),
    BinauralPreset("Schumann", 7.83),
    BinauralPreset("Alpha", 10.00),
    BinauralPreset("Beta", 20.00),
    BinauralPreset("Gamma", 40.00)
)