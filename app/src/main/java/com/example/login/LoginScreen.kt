package com.example.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.login.ui.components.PrimaryButton
import com.google.firebase.auth.FirebaseAuth

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\d]).{8,}$")
    return regex.matches(password)
}

@Composable
fun LoginScreen(onSignupClick: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    // Validation errors
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val firebaseAuth = FirebaseAuth.getInstance()

    if (isLoggedIn) {
        HomeScreen(
            userEmail = username,
            onLogoutClick = {
                firebaseAuth.signOut()
                isLoggedIn = false
                username = ""
                password = ""
            }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(300.dp)
                .clip(RoundedCornerShape(bottomStart = 80.dp, bottomEnd = 80.dp))
                .background(Color(0xFF0D47A1))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "App Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(620.dp)
                    .padding(top = 32.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Back!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorResource(id = R.color.purple_700),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Please log in to continue",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            emailError = if (username.isEmpty()) {
                                "Email must be entered"
                            } else if (!isValidEmail(username)) {
                                "Invalid email format"
                            } else {
                                ""
                            }
                        },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email Icon")
                        },
                        isError = emailError.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )
                    if (emailError.isNotEmpty()) {
                        Text(
                            text = emailError,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 8.dp, bottom = 12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = if (password.isEmpty()) {
                                "Password must be entered"
                            } else if (!isValidPassword(password)) {
                                "Password must be at least 8 characters and include uppercase, lowercase, number and special character"
                            } else {
                                ""
                            }
                        },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password Icon")
                        },
                        trailingIcon = {
                            val icon =
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    icon,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )
                    if (passwordError.isNotEmpty()) {
                        Text(
                            text = passwordError,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 8.dp, bottom = 20.dp)
                        )
                    }

                    PrimaryButton(
                        text = "Log In",
                        onClick = {
                            var valid = true

                            if (username.isEmpty()) {
                                emailError = "Email must be entered"
                                valid = false
                            } else if (!isValidEmail(username)) {
                                emailError = "Invalid email format"
                                valid = false
                            }

                            if (password.isEmpty()) {
                                passwordError = "Password must be entered"
                                valid = false
                            } else if (!isValidPassword(password)) {
                                passwordError = "Password must be at least 8 characters and include uppercase, lowercase, number and special character"
                                valid = false
                            }

                            if (valid) {
                                firebaseAuth.signInWithEmailAndPassword(username, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            dialogMessage = "Login successful!"
                                            showDialog = true
                                        } else {
                                            dialogMessage = "Login failed: ${task.exception?.message}"
                                            showDialog = true
                                        }
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(50.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account? ",
                            color = colorResource(id = R.color.gray_500)
                        )
                        Text(
                            text = "Sign up",
                            color = colorResource(id = R.color.purple_700),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { onSignupClick() }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        CuteDialog(
            message = dialogMessage,
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
                if (dialogMessage == "Login successful!") {
                    isLoggedIn = true
                }
            }
        )
    }
}

@Composable
fun CuteDialog(
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 16.dp,
            color = Color(0xFFFFF8E1)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.img_5),
                    contentDescription = "Cute Icon",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (message.contains("successful")) "Yay! 🎉" else "Oops! 😟",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (message.contains("successful")) Color(0xFF4CAF50) else Color(0xFFF44336)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = message,
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onConfirm,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7B1FA2)
                    ),
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text(text = "OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    MaterialTheme {
        LoginScreen(onSignupClick = {})
    }
}
