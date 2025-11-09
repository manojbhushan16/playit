package com.example.playit.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playit.R
import com.example.playit.core.Constants
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onSignUp: () -> Unit) {
    val context = LocalContext.current as Activity
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Google Sign-In setup
    // Use Web Client ID from google-services.json (client_type: 3)
    // This is the Web Client ID for com.example.playit
    val webClientId = "299849895582-qpu3e721tccckqmi1o5rhrldmp5206c0.apps.googleusercontent.com"
    
    val signInClient: SignInClient = Identity.getSignInClient(context)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(webClientId)
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = signInClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    Log.d(Constants.LOG_TAG, "Google Sign-In successful, ID token received")
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    isLoading = true
                    auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task: Task<AuthResult> ->
                        isLoading = false
                        if (task.isSuccessful) {
                            Log.d(Constants.LOG_TAG, "Firebase authentication successful")
                            onLoginSuccess()
                        } else {
                            // Log error details
                            val exception = task.exception
                            val exceptionMessage = exception?.message ?: "Unknown error"
                            Log.e(Constants.LOG_TAG, "Firebase Auth Failed: $exceptionMessage", exception)
                            Toast.makeText(context, "Authentication Failed: $exceptionMessage", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.e(Constants.LOG_TAG, "Google Sign-In failed: No ID token received")
                    Toast.makeText(context, "Google Sign-In failed: No ID token", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "Google Sign-In error: ${e.message}", e)
                Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.d(Constants.LOG_TAG, "Google Sign-In canceled by user")
            isLoading = false
            // User canceled - no need to show error
        } else {
            Log.e(Constants.LOG_TAG, "Google Sign-In failed with result code: ${result.resultCode}")
            isLoading = false
            Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
        }
    }









Box(modifier = Modifier.fillMaxSize()) {
        // Palette-aligned gradient background
        val gradient = if (MaterialTheme.colorScheme.background.luminance() > 0.5f) {
            Brush.verticalGradient(listOf(Color(0xFFEBEBEB), Color(0xFF0BA6DF)))
        } else {
            Brush.verticalGradient(listOf(Color(0xFF121212), Color(0xFF465C88)))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isLightMode = MaterialTheme.colorScheme.background.luminance() > 0.5f
            val logoColor = if (isLightMode) Color.Black else Color.White
            Image(
                painter = painterResource(id = R.drawable.songs),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(logoColor)
            )

            Text(
                text = "PlayIt",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down)}
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // Handle login on "Done" press
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task: Task<AuthResult> ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        onLoginSuccess()
                                    } else {
                                        val errorMessage = task.exception?.message ?: "Login failed"
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                        }
                    }
                ),
                trailingIcon = {
                    if (password.isNotEmpty()) {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                painter = painterResource(id = if (showPassword) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                                contentDescription = "Toggle Password Visibility",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task: Task<AuthResult> ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    onLoginSuccess()
                                } else {
                                    val errorMessage = task.exception?.message ?: "Login failed"
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Log In", fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Sign Up Link
            TextButton(onClick = onSignUp) {
                Text("New to PlayIt? Sign Up", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                "Or",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            // Google Sign-In Button
            Button(
                onClick = {
                    isLoading = true
                    signInClient.beginSignIn(signInRequest)
                        .addOnSuccessListener { result ->
                            isLoading = false
                            try {
                                // Wrap PendingIntent in IntentSenderRequest and launch it
                                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent).build()
                                launcher.launch(intentSenderRequest)
                            } catch (e: Exception) {
                                Log.e(Constants.LOG_TAG, "Error launching Google Sign-In: ${e.message}", e)
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            isLoading = false
                            val errorMessage = exception.message ?: "Unknown error"
                            Log.e(Constants.LOG_TAG, "Google Sign-In beginSignIn failed: $errorMessage", exception)
                            Toast.makeText(context, "Google Sign-In failed: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = "Google Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign in with Google")
            }

            // Loading Indicator
            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
