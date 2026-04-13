package com.helpful.healingnotes.util

import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Short
import kotlin.Int
import kotlin.ShortArray

object WavExporter {

    fun export(file: File, samples: ShortArray, sampleRate: Int = 44100) {

        val output = DataOutputStream(FileOutputStream(file))

        val byteRate = sampleRate * 2 * 2

        output.writeBytes("RIFF")
        output.writeInt(Integer.reverseBytes(36 + samples.size * 2))
        output.writeBytes("WAVE")

        output.writeBytes("fmt ")
        output.writeInt(Integer.reverseBytes(16))
        output.writeShort(Short.reverseBytes(1).toInt())
        output.writeShort(Short.reverseBytes(2).toInt())
        output.writeInt(Integer.reverseBytes(sampleRate))
        output.writeInt(Integer.reverseBytes(byteRate))
        output.writeShort(Short.reverseBytes((2 * 2).toShort()).toInt())
        output.writeShort(Short.reverseBytes(16).toInt())

        output.writeBytes("data")
        output.writeInt(Integer.reverseBytes(samples.size * 2))

        samples.forEach {
            output.writeShort(Short.reverseBytes(it).toInt())
        }

        output.close()
    }
}