package com.example.hushtrack

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SignInScreen(navController: NavController, authManager: FireBaseAuthManager) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .wrapContentSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(24.dp),
            horizontalAlignment =  Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Sign In",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            var email by remember { mutableStateOf("") }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            

            Spacer(modifier = Modifier.height(16.dp))

            var password by remember { mutableStateOf("") }
            var passwordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {Text("Password")},
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = {passwordVisible = !passwordVisible}) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { navController.navigate("forgot") },
                modifier = Modifier.align(Alignment.End),

            ) {
                Text("Forgot Password", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val uid = authManager.loginUser(email, password)

                        Log.d("SignInScreen", "Logged in UID: $uid")

                        if (uid != null) {
                            val userType = authManager.getUserType(uid)

                            Log.d("SignInScreen", "UserType: $userType")

                            withContext(Dispatchers.Main) {
                                if (userType == "admin") {
                                    Log.d("SignInScreen", "Navigating to Admin Screen")
                                    navController.navigate("admin/$uid") {
                                        popUpTo("signin") { inclusive = true}
                                    }
                                } else {
                                    Log.d("SignInScreen", "Navigating to Home Screen")
                                    navController.navigate("home/$uid") {
                                        popUpTo("signin") { inclusive = true}
                                    }
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                errorMessage = "Login Failed"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
//                    .background(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text(
                    text = "Sign In",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Don't have an Account?",
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick = { navController.navigate("signup") },
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text("Register", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
            errorMessage?.let { Text(it, color = Color.Red) }
        }
    }
}

//@Preview(showSystemUi = true, showBackground = true)
//@Composable
//fun SignInScreenPreview() {
//    SignInScreen()
//}