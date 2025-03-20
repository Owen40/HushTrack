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
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen() {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier.padding(start = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Edit Profile",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment =  Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = {Text("Username")},
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AlternateEmail,
                        contentDescription = "Username",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                placeholder = { Text("User202501")}
            )
            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = "example@email.com",
                onValueChange = { username = it },
                label = {Text("Email")},
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray
                ),
                placeholder = { Text("User202501")}
            )
            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = {Text("Username")},
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AlternateEmail,
                        contentDescription = "Username",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                placeholder = { Text("User202501")}
            )

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = "+254712345678",
                onValueChange = {  },
                label = {Text("Username")},
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = { Text("User202501")}
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp)),
                onClick = {}
            ) {
                Text(text = "Update")
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}