package com.example.hushtrack.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DecibelMeter(isRecording: Boolean, context: Context) {
    var decibels by remember { mutableStateOf(0.0) }
    var hasPermission by remember { mutableStateOf(false) }
    var audioRecord: AudioRecord? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    if (!hasPermission) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    LaunchedEffect(isRecording, hasPermission) {
        if (isRecording && hasPermission) {
            coroutineScope.launch(Dispatchers.IO) {
                val bufferSize = AudioRecord.getMinBufferSize(
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )

                val buffer = ShortArray(bufferSize)
                audioRecord.startRecording()

                while (isRecording) {
                    val read = audioRecord.read(buffer, 0, bufferSize)
                    val amplitude = buffer.take(read).maxOrNull()?.toDouble() ?: 0.0
                    decibels = if (amplitude > 0) 20 * Math.log10(amplitude) else 0.0
                }

                audioRecord?.stop()
                audioRecord?.release()
            }
        } else {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        }
    }

    Text(text = "Noise Level: ${decibels.toInt()} dB", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(20.dp))
}