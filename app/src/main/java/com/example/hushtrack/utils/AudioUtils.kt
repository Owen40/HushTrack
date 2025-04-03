package com.example.hushtrack.utils

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import java.io.File

fun startRecording(recorder: MediaRecorder, file: File) {
    recorder.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setOutputFile(file.absolutePath)
        prepare()
        start()
        Log.d("AudioUtils", "Recording Started: ${file.absolutePath}")
    }
}

fun stopRecording(recorder: MediaRecorder) {
    recorder.apply {
        stop()
        release()
    }
}

fun startPlayback(player: MediaPlayer, file: File) {
    player.apply {
        setDataSource(file.absolutePath)
        prepare()
        start()
    }
}

fun stopPlayback(player: MediaPlayer) {
    player.stop()
    player.release()
}