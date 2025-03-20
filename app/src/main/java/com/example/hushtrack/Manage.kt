package com.example.hushtrack

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun manageReportScreen() {
    var selectedStatus by remember { mutableStateOf("Under Investigation") }
    val statusOptions = listOf("Pending Review", "Under Investigation", "Resolved", "Dismissed")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    text = "Manage Report",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoCard(title = "Reporter", value = "Anonymous")
                InfoCard(title = "Noise Type", value = "Loud Noise")
            }

            Spacer(modifier = Modifier.height(30.dp))
            InfoBox(title = "Location", value = "Equity, Kasarani")

            Spacer(modifier = Modifier.height(30.dp))
            InfoBox(title = "Report Decription", value = "Supporting line text lorem ipsum dolor sit amet, consectetur")

            Spacer(modifier = Modifier.height(70.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedButton(onClick = {}) {
                    Text("Play Recording")
                }
                OutlinedButton(onClick = {}) {
                    Text("Stop Playback")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            Text("Report Status", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            DropdownMenuBox(selectedStatus, statusOptions) { selectedStatus = it }

            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
                    .height(50.dp)
            ) {
                Text("Update Report", fontSize = 18.sp)
            }
        }
    }

}

@Composable
fun InfoCard(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
//            .weight(1f)
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(value)
        }
    }
}

@Composable
fun InfoBox(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(value)
        }
    }
}

@Composable
fun DropdownMenuBox(selectedOption: String, options: List<String>, onSelectionChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedOption, modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "DropDown Icon")
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false}, modifier = Modifier.fillMaxWidth()) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onSelectionChange(option)
                    expanded = false
                },
                    text = { Text(option)})
            }
        }
    }
}