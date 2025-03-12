package com.example.hushtrack.utils

import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.File

fun startRecording(recorder: MediaRecorder, file: File) {
    recorder.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        setOutputFile(file.absolutePath)
        prepare()
        start()
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