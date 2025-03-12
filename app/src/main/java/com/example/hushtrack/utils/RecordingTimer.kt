package com.example.hushtrack.utils

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecordingTimer(
    isRecording: Boolean,
    onTimeout: () -> Unit
) {
    var elapsedTime by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isRecording) {
        if (isRecording) {
            coroutineScope.launch {
                while (elapsedTime < 90) {
                    delay(1000L)
                    elapsedTime++
                }
                onTimeout()
            }
        } else {
            elapsedTime = 0
        }
    }

    Text(text = "Recording: $elapsedTime / 90 sec", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(20.dp))
}