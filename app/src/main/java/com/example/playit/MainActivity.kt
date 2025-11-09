package com.example.playit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.playit.ui.screens.MainScreen
import com.example.playit.ui.theme.PlayItTheme
import dagger.hilt.android.AndroidEntryPoint
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.playit.core.prefs.ThemePreferences
import androidx.compose.runtime.remember

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Toast.makeText(this, "Permissions granted - you can now access your music!", Toast.LENGTH_SHORT).show()
        } else {

        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        installSplashScreen()
        
        // Request permissions
        checkAndRequestPermissions()
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        
        setContent {
            val context = this
            val themePrefs = remember { ThemePreferences(context) }
            val isDark by themePrefs.isDarkMode.collectAsState(initial = false)
            PlayItTheme(darkTheme = isDark) {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5f
                val statusBarColor = MaterialTheme.colorScheme.background
                val navBarColor = MaterialTheme.colorScheme.background
                SideEffect {
                    systemUiController.setStatusBarColor(color = statusBarColor, darkIcons = useDarkIcons)
                    systemUiController.setNavigationBarColor(color = navBarColor, darkIcons = useDarkIcons)
                }
                MainScreen()
            }
        }
    }
}






