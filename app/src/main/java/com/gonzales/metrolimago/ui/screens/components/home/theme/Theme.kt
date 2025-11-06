package com.gonzales.metrolimago.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 🎨 Colores principales
private val MetroGreen = Color(0xFF00A86B)
private val MetroRed = Color(0xFFE53935)
private val MetroYellow = Color(0xFFFFC107)
private val MetroBlue = Color(0xFF1976D2)

// 🌙 Esquema de colores modo oscuro
private val DarkColorScheme = darkColorScheme(
    primary = MetroGreen,
    secondary = MetroYellow,
    tertiary = MetroBlue,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

// ☀️ Esquema de colores modo claro
private val LightColorScheme = lightColorScheme(
    primary = MetroGreen,
    secondary = MetroYellow,
    tertiary = MetroBlue,
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

// 🌆 Tema general de la app
@Composable
fun MetroLimaGOTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// 🎨 Paleta personalizada para otras partes del proyecto
object MetroColors {
    val Linea1 = Color(0xFFE53935)   // Rojo
    val Linea2 = Color(0xFFFFC107)   // Amarillo
    val Linea3 = Color(0xFF1976D2)   // Azul 🔹 (nueva línea agregada)
    val SuccessGreen = Color(0xFF4CAF50)
    val WarningOrange = Color(0xFFFF9800)
    val ErrorRed = Color(0xFFF44336)
}
