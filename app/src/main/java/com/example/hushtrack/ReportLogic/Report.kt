package com.example.hushtrack.ReportLogic

import com.google.firebase.Timestamp

data class Report(
    val id: String = "",
    val timestamp: Any? = null,
    val status: String = "",
    val location: String = "",
    val reporter: String = "",
    val noiseType: String = "",
    val description: String = "",
    val audioUrl: String = ""
) {
    val formattedTimeStamp: Long
        get() = when (timestamp) {
            is Timestamp -> timestamp.seconds * 1000
            is Long -> timestamp
            else -> 0L
        }
}
