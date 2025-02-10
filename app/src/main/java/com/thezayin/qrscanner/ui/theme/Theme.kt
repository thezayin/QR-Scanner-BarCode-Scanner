package com.thezayin.qrscanner.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DefaultLightColorScheme = lightColorScheme(
    primary = Purple40,
    tertiary = Pink40,
    //background color
    background = Color(0xFFDEDEDE),
    //icon and text color
    onSurface = Color(0xFF000000),
    //card color
    surfaceContainer = (Color(0xFFFFFFFF)),
    //text field color
    secondary = (Color(0xFFf3f3f3)),
)

private val DefaultDarkColorScheme = darkColorScheme(
    primary = Purple80,
    tertiary = Pink80,

    //background color
    background = Color(0xFF121212),
    //icon and text color
    onSurface = Color(0xFFE3E3E3),
    //card color
    surfaceContainer = (Color(0xFF353935)),
    //text field color
    secondary = Color(0xFF353935),
)

@Composable
fun QRScannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    userSelectedPrimary: Color? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val baseColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DefaultDarkColorScheme
        else -> DefaultLightColorScheme
    }

    val finalColorScheme = if (userSelectedPrimary != null) {
        baseColorScheme.copy(primary = userSelectedPrimary)
    } else {
        baseColorScheme
    }

    MaterialTheme(
        colorScheme = finalColorScheme, typography = Typography, content = content
    )
}