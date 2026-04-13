package com.helpful.healingnotes.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Process
import com.helpful.healingnotes.data.ToneConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.sin

class ToneEngine {

    @Volatile
    private var isPlaying = false
    private val mutex = Mutex()

    fun stop() {
        isPlaying = false
    }

    suspend fun play(configProvider: () -> ToneConfig) = mutex.withLock {

        withContext(Dispatchers.Default) {
            // Boost thread priority to reduce pre-emption crackling
            val tid = Process.myTid()
            val oldPriority = Process.getThreadPriority(tid)
            try {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
            } catch (e: Exception) {
                // Ignore errors
            }

            val sampleRate = 44100
            val encoding = AudioFormat.ENCODING_PCM_FLOAT
            val channelConfig = AudioFormat.CHANNEL_OUT_STEREO

            val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, encoding)
            // Large buffer (approx 1 second) to absorb emulator/system jitter
            val trackBufferSizeInBytes = (minBufferSize * 12).coerceAtLeast(sampleRate * 2 * 4)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(encoding)
                        .setSampleRate(sampleRate)
                        .setChannelMask(channelConfig)
                        .build()
                )
                .setBufferSizeInBytes(trackBufferSizeInBytes)
                .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                .build()

            var leftPhase = 0.0
            var rightPhase = 0.0
            var sampleIndex = 0L
            var currentFade = 0.0f

            // 1024 frames provides a good balance between latency and stability
            val framesPerBuffer = 1024
            val audioBuffer = FloatArray(framesPerBuffer * 2)

            val twoPi = 2.0 * PI
            val invSampleRate = 1.0 / sampleRate
            val maxGain = 0.7f

            val initialConfig = configProvider()
            val rampDurationSec = initialConfig.rampMinutes * 60.0
            val fadeInSec = initialConfig.fadeInSec
            val fadeOutSec = initialConfig.fadeOutSec
            val durationSec = initialConfig.durationSec?.toDouble()
            val totalSamples = initialConfig.durationSec?.let { it.toLong() * sampleRate }

            try {
                track.play()
                isPlaying = true

                while (isPlaying && isActive && (totalSamples == null || sampleIndex < totalSamples)) {
                    // Fetch latest config for dynamic updates (e.g. slider changes)
                    val currentConfig = configProvider()
                    val baseFreq = currentConfig.baseFreq
                    val binauralStart = currentConfig.binauralStart
                    val binauralDiff = currentConfig.binauralEnd - currentConfig.binauralStart
                    val swapEars = currentConfig.swapEars

                    val framesToGenerate = if (totalSamples != null) {
                        minOf(framesPerBuffer.toLong(), totalSamples - sampleIndex).toInt()
                    } else {
                        framesPerBuffer
                    }

                    val tStart = sampleIndex.toDouble() * invSampleRate
                    val rampProgress = if (rampDurationSec > 0) (tStart / rampDurationSec).coerceIn(0.0, 1.0) else 1.0
                    val binaural = binauralStart + binauralDiff * rampProgress

                    val leftFreq = if (swapEars) baseFreq + binaural else baseFreq
                    val rightFreq = if (swapEars) baseFreq else baseFreq + binaural

                    val leftStep = (twoPi * leftFreq * invSampleRate)
                    val rightStep = (twoPi * rightFreq * invSampleRate)

                    val targetFade = when {
                        fadeInSec > 0 && tStart < fadeInSec -> (tStart / fadeInSec).toFloat()
                        durationSec != null && fadeOutSec > 0 && tStart > durationSec - fadeOutSec ->
                            ((durationSec - tStart) / fadeOutSec).coerceIn(0.0, 1.0).toFloat()
                        else -> 1.0f
                    }

                    val fadeStep = (targetFade - currentFade) / framesToGenerate
                    
                    // Use local vars for the tight loop to improve performance
                    var lp = leftPhase
                    var rp = rightPhase
                    var f = currentFade
                    var bp = 0

                    for (i in 0 until framesToGenerate) {
                        f += fadeStep
                        val gain = f * maxGain

                        audioBuffer[bp++] = (sin(lp) * gain).toFloat()
                        audioBuffer[bp++] = (sin(rp) * gain).toFloat()

                        lp += leftStep
                        rp += rightStep

                        if (lp >= twoPi) lp -= twoPi
                        if (rp >= twoPi) rp -= twoPi
                    }

                    leftPhase = lp
                    rightPhase = rp
                    currentFade = f

                    val result = track.write(audioBuffer, 0, bp, AudioTrack.WRITE_BLOCKING)
                    if (result < 0) break
                    sampleIndex += framesToGenerate
                }
            } finally {
                withContext(NonCancellable) {
                    if (currentFade > 0.001f) {
                        val fadeOutSamples = (sampleRate * 0.15).toInt()
                        val buffer = FloatArray(512)
                        var remaining = fadeOutSamples
                        val startFade = currentFade
                        val finalBaseFreq = configProvider().baseFreq

                        while (remaining > 0) {
                            var bp = 0
                            val chunk = minOf(remaining, 256)
                            for (i in 0 until chunk) {
                                val fade = startFade * (remaining.toDouble() / fadeOutSamples).toFloat()
                                val gain = fade * maxGain
                                buffer[bp++] = (sin(leftPhase) * gain).toFloat()
                                buffer[bp++] = (sin(rightPhase) * gain).toFloat()
                                leftPhase = (leftPhase + (twoPi * finalBaseFreq * invSampleRate)) % twoPi
                                rightPhase = (rightPhase + (twoPi * finalBaseFreq * invSampleRate)) % twoPi
                                remaining--
                            }
                            track.write(buffer, 0, bp, AudioTrack.WRITE_BLOCKING)
                        }
                    }

                    try { track.stop() } catch (e: Exception) {}
                    track.release()
                    isPlaying = false
                    try { Process.setThreadPriority(tid, oldPriority) } catch (e: Exception) {}
                }
            }
        }
    }
}