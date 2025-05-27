package com.example.gametracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    primaryContainer = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    background = Color.Black, // Fondo oscuro
    surface = Color.Black,     // Fondo de superficies oscuras
    onPrimary = Color.Black,   // Texto negro sobre el color primario
    onSecondary = Color.Black,
    onBackground = Color.White, // Texto blanco sobre fondo oscuro
    onSurface = Color.White     // Texto blanco sobre superficies oscuras
)

// Define el esquema de colores para el modo claro (puedes personalizarlo como quieras)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    primaryContainer = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    background = Color.White, // Fondo blanco
    surface = Color.White,     // Superficies blancas
    onPrimary = Color.White,   // Texto blanco sobre el color primario
    onSecondary = Color.Black,
    onBackground = Color.Black, // Texto negro sobre fondo blanco
    onSurface = Color.Black     // Texto negro sobre superficies blancas
)

@Composable
fun GameTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Usa el tema oscuro si el sistema está en ese modo
    content: @Composable () -> Unit
) {
    // Selecciona el esquema de colores según el modo (oscuro o claro)
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,  // Usa tu configuración de tipografía personalizada
        content = content       // Pasa el contenido que se debe envolver con el tema
    )
}