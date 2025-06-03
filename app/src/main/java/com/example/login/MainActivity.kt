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

        FirebaseApp.initializeApp(this)

        setContent {
            var isLoginScreen by remember { mutableStateOf(true) }
            var isHomeScreen by remember { mutableStateOf(false) }
            var userEmail by remember { mutableStateOf("") }
            MaterialTheme {
                when {
                    isHomeScreen -> {
                        HomeScreen(userEmail = userEmail, onLogoutClick = {
                            isHomeScreen = false
                            isLoginScreen = true
                            userEmail = ""
                        })
                    }

                    isLoginScreen -> {
                        LoginScreen(onSignupClick = { isLoginScreen = false })
                    }
                    else -> {
                        SignupScreen(
                            onSignupSuccess = {
                                isHomeScreen = true   // Show home screen on signup success
                            },
                            onLoginClick = { isLoginScreen = true }
                        )
                    }
                }
            }
        }
    }
}
