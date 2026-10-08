package com.example.dsy1105_006d_lc7.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Esquema de colores oscuros (tema automotriz industrial).
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = OnPrimaryDark,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = OnPrimaryDark,
    tertiary = StockNormal,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = StockCritical
)

/**
 * Esquema de colores claros.
 */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryVariant,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = OnPrimaryDark,
    secondary = SecondaryVariant,
    onSecondary = OnPrimaryDark,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = OnSecondaryDark,
    tertiary = StockNormal,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = StockCritical
)

/**
 * Tema principal de la aplicación de inventario automotriz.
 * Soporta tema claro/oscuro con Dynamic Color en Android 12+.
 */
@Composable
fun DSY1105006DLC7Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}