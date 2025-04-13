package com.example.hushtrack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hushtrack.ReportLogic.Report
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportCard(report: Report, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable{ onClick() },
        shape = RoundedCornerShape(8.dp),
//        elevation = 4.dp,
//        backgroundColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Reporter", fontWeight = FontWeight.Bold)
                Text("TimeStamp", fontWeight = FontWeight.Bold)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(report.reporter)
//                Text(report.timestamp)
                val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    .format(Date(report.formattedTimeStamp))
                Text(formattedDate)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Status", fontWeight = FontWeight.Bold)
            Text(report.status)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Location", fontWeight = FontWeight.Bold)
            Text(report.location)
        }
    }
}