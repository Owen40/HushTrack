package com.example.hushtrack

import androidx.compose.foundation.background
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientReportDescScreen() {
    var noiseType by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var noiseDescription by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false ) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp)
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
                    text = "New Report",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(text = "Noise Type", color = MaterialTheme.colorScheme.onBackground)
            Box{
                OutlinedTextField(
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    value = noiseType,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    shape = RoundedCornerShape(10.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = { isDropdownExpanded = true}
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "DropDown",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                )
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Loud Music", "Construction", "Crowds", "Animals").forEach { type ->
                        DropdownMenuItem(
                            onClick = {
                                noiseType = type
                                isDropdownExpanded = false
                            },
                            text = {
                                Text(type)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = "Location", color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray
                ),
                value = location,
                onValueChange = { location = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .height(120.dp),
                placeholder = { Text("Enter the location", color = MaterialTheme.colorScheme.onBackground.copy(0.5f))}
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = "Noise Description", color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray
                ),
                value = noiseDescription,
                onValueChange = { noiseDescription = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .height(120.dp),
                placeholder = { Text("Describe the noise you wish to report", color = MaterialTheme.colorScheme.onBackground.copy(0.5f))}
            )

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = {
                    println("Noise Type: $noiseType, Location: $location, Noise Description: $noiseDescription")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            ) {
                Text("Submit")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ReportPreview() {
    ClientReportDescScreen()
}