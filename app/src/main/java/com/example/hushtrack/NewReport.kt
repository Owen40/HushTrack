package com.example.hushtrack

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
//import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.hushtrack.utils.DecibelMeter
import com.example.hushtrack.utils.RecordingTimer
import com.example.hushtrack.utils.startPlayback
import com.example.hushtrack.utils.startRecording
import com.example.hushtrack.utils.stopPlayback
import com.example.hushtrack.utils.stopRecording
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ReportScreen(navController: NavController,  context: Context, uid: String, authManager: FireBaseAuthManager) {
    val recorder = remember { MediaRecorder() }
    val player = remember { MediaPlayer() }
    val audioFile = remember { mutableStateOf(File(context.cacheDir, "audio_${System.currentTimeMillis()}.mp4")) }
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }

//    Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                startRecording(recorder, audioFile.value)
            } else {
                Toast.makeText(context, "Permission required!", Toast.LENGTH_LONG).show()
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "New Report",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 5.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (isRecording) {
            RecordingTimer(isRecording = isRecording, onTimeout = {
                stopRecording(recorder)
                isRecording = false
            })
        }

//        if (isRecording) {
//            DecibelMeter(isRecording = isRecording, context = context)
//        }

        Spacer(modifier = Modifier.height(18.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        startRecording(recorder, audioFile.value)
                        isRecording = true
                    }
                },
                enabled = !isRecording,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .height(51.dp)
                    .fillMaxWidth(0.4f),
            ) {
                Text(
                    text = "Record"
                )
            }

            OutlinedButton(
                onClick = {
                    stopRecording(recorder)
                    isRecording = false
                },
                enabled = isRecording,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .height(51.dp)
                    .fillMaxWidth(0.8f)
            ) {
                Text(
                    text = "Stop Recording"
                )
            }
        }

        Spacer(modifier = Modifier.height(35.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = {
                    startPlayback(player, audioFile.value)
                    isPlaying = true
                },
                enabled = !isPlaying && audioFile.value.exists(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .height(51.dp)
                    .fillMaxWidth(0.5f)
            ) {
                Text(
                    text = "play"
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedButton(
                onClick = {
                    stopPlayback(player)
                    isPlaying = false
                },
                enabled = isPlaying,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .height(51.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Pause"
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (audioFile.value.exists()) {
                    audioFile.value.delete()
                    Toast.makeText(context, "Recording Deleted", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(51.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                isUploading = true
                CoroutineScope(Dispatchers.IO).launch {
                    val downloadUrl = authManager.uploadAudioFile(uid, audioFile.value)
                    val encodeUrl = URLEncoder.encode(downloadUrl, StandardCharsets.UTF_8.toString())
                    withContext(Dispatchers.Main) {
                        isUploading = false
                        if(downloadUrl != null) {
                            navController.navigate("new-report-details/$uid/$encodeUrl")
                        } else {
                            Toast.makeText(context, "Upload Failed", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            enabled = audioFile.value.exists() && !isUploading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
                .height(51.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
        ) {
            Text(
                if (isUploading) "Uploading..." else "Continue"
            )
        }
    }
}

//@Preview
//@Composable
//fun previewReportScreen() {
//    ReportScreen()
//}