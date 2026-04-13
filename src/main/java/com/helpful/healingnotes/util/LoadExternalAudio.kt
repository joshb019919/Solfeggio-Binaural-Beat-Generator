package com.helpful.healingnotes.util

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

fun playAudioFile(context: Context, uri: Uri) {
    val mediaPlayer = MediaPlayer().apply {
        setDataSource(context, uri)
        prepare()
        start()
    }
}