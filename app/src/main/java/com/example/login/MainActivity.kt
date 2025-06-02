package com.example.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            var isLoginScreen by remember { mutableStateOf(true) }

            MaterialTheme {
                if (isLoginScreen) {
                    LoginScreen(onSignupClick = { isLoginScreen = false })
                } else {
                    SignupScreen(onLoginClick = { isLoginScreen = true })
                }
            }
        }
    }
}

