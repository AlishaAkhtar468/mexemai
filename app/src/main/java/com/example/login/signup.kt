package com.example.login

import android.util.Log
import android.util.Patterns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.login.ui.components.PrimaryButton
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.compose.rememberLauncherForActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn



@Composable
fun SignupScreen( onSignupSuccess: () -> Unit, onLoginClick: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    Log.d("TAG", "SignupScreen: ${task.result.credential}")
                    if (task.isSuccessful) {
                        dialogMessage = "Google Sign-In successful!"
                        showDialog = true
                        onSignupSuccess()
                    } else {
                        dialogMessage = "Google Sign-In failed: ${task.exception?.message}"
                        showDialog = true
                    }
                }
        } catch (e: ApiException) {
            dialogMessage = "Google Sign-In error: ${e.message}"
            showDialog = true
        }
    }
    val nameFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        nameFocusRequester.requestFocus()
    }

    fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    fun isPhoneValid(phone: String): Boolean =
        phone.length == 11 && phone.all { it.isDigit() }

    fun isUsernameValid(name: String): Boolean =
        name.isNotEmpty() && name.all { it.isLetter() }

    fun isPasswordValid(password: String): Boolean {
        val passwordRegex =
            Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$")
        return passwordRegex.matches(password)
    }

    fun validateFields(): Boolean {
        var isValid = true

        nameError = if (name.isEmpty()) {
            isValid = false
            "Name is required"
        } else if (!isUsernameValid(name)) {
            isValid = false
            "Only characters allowed"
        } else ""

        emailError = if (email.isEmpty()) {
            isValid = false
            "Email is required"
        } else if (!isEmailValid(email)) {
            isValid = false
            "Invalid email address"
        } else ""

        phoneError = if (phone.isEmpty()) {
            isValid = false
            "Phone number is required"
        } else if (!isPhoneValid(phone)) {
            isValid = false
            "Phone number must be exactly 11 digits"
        } else ""

        passwordError = if (password.isEmpty()) {
            isValid = false
            "Password is required"
        } else if (!isPasswordValid(password)) {
            isValid = false
            "Password must be at least 8 characters and include uppercase, lowercase, number, and special character"
        } else ""

        return isValid
    }

    // Real-time validation helpers:
    fun validateNameInput(input: String): String =
        when {
            input.isEmpty() -> "Name is required"
            !isUsernameValid(input) -> "Only characters allowed"
            else -> ""
        }

    fun validateEmailInput(input: String): String =
        when {
            input.isEmpty() -> "Email is required"
            !isEmailValid(input) -> "Invalid email address"
            else -> ""
        }

    fun validatePhoneInput(input: String): String =
        when {
            input.isEmpty() -> "Phone number is required"
            !isPhoneValid(input) -> "Phone number must be exactly 11 digits"
            else -> ""
        }

    fun validatePasswordInput(input: String): String =
        when {
            input.isEmpty() -> "Password is required"
            !isPasswordValid(input) -> "Password must be at least 8 characters and include uppercase, lowercase, number, and special character"
            else -> ""
        }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Error") },
            text = { Text(dialogMessage) }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { onLoginClick() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(bottomStart = 80.dp, bottomEnd = 80.dp))
                .background(Color(0xFF0D47A1))
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Image(
                painter = painterResource(id = R.drawable.img_4),
                contentDescription = "App Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorResource(id = R.color.purple_700),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Sign up to create an account",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = validateNameInput(it)
                        },
                        label = { Text("Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                        isError = nameError.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(nameFocusRequester)
                    )
                    if (nameError.isNotEmpty()) {
                        Text(nameError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = validateEmailInput(it)
                        },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        isError = emailError.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (emailError.isNotEmpty()) {
                        Text(emailError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            phoneError = validatePhoneInput(it)
                        },
                        label = { Text("Phone (11 digits)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                        isError = phoneError.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (phoneError.isNotEmpty()) {
                        Text(phoneError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = validatePasswordInput(it)
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                        trailingIcon = {
                            val icon =
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passwordError.isNotEmpty()) {
                        Text(passwordError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryButton(
                        text = "Sign Up",
                        onClick = {
                            if (validateFields()) {
                                auth.createUserWithEmailAndPassword(email.trim(), password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            dialogMessage = "Sign up successful!"
                                            showDialog = true
                                            onSignupSuccess()  // Navigate to home here
                                        } else {
                                            dialogMessage = "Error: ${task.exception?.message}"
                                            showDialog = true
                                        }
                                    }
                            } else {
                                dialogMessage = "Please correct the errors above."
                                showDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(50.dp)
                    )
                    Button(
                        onClick = {
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken("921684541764-tfa8j3ds98v9o3q4r243ks9i0oqp497m.apps.googleusercontent.com")
                                .requestEmail()
                                .build()
                            val signInClient = GoogleSignIn.getClient(context, gso)
                            launcher.launch(signInClient.signInIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.img_1),
                            contentDescription = "Google Sign-In",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign in with Google", color = Color.Black)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Already have an account? ", color = Color.Gray)
                        Text(
                            text = "Log in",
                            color = colorResource(id = R.color.purple_700),
                            modifier = Modifier.clickable { onLoginClick() }
                        )
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewSignupScreen() {
    MaterialTheme {
        SignupScreen(
            onSignupSuccess = {},
            onLoginClick = {}
        )
    }
}

