package com.example.playit.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme


// Define the Shapes for the app
val shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

// Define the Light Color Scheme for the app
val lightColorScheme = lightColorScheme(
    primary = Color(0xFF0BA6DF),
    secondary = Color(0xFFFAA533),
    tertiary = Color(0xFFEF7722),
    background = Color(0xFFEBEBEB),
    surface = Color(0xFFF7FAFC),
    surfaceVariant = Color(0xFFE1EDF3),
    onPrimary = Color(0xFF08212A),
    onSecondary = Color(0xFF312107),
    onTertiary = Color(0xFF331502),
    onBackground = Color(0xFF101418),
    onSurface = Color(0xFF172028),
    onSurfaceVariant = Color(0xFF475461),
)

// Define the Dark Color Scheme for the app
val darkColorScheme = darkColorScheme(
    primary = Color(0xFFFF7A30),
    secondary = Color(0xFF465C88),
    tertiary = Color(0xFFE9E3DF),
    background = Color(0xFF121212),
    surface = Color(0xFF181C20),
    surfaceVariant = Color(0xFF222830),
    onPrimary = Color(0xFF1B0B00),
    onSecondary = Color(0xFFE6EBF6),
    onTertiary = Color(0xFF131515),
    onBackground = Color(0xFFE9E3DF),
    onSurface = Color(0xFFE5E8ED),
    onSurfaceVariant = Color(0xFFB3BCC7),
)

@Composable
fun PlayItTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Select color scheme based on dark or light theme
    val colors = if (darkTheme) darkColorScheme else lightColorScheme

    // Wrap the MaterialTheme with color scheme, typography, and shapes
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,  // Use custom Typography from Type.kt
        shapes = shapes,  // Set the custom shapes
        content = content  // Apply the theme to the content
    )
}
