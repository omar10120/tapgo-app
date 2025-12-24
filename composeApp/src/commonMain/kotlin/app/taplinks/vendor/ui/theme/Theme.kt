package app.taplinks.vendor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = VendorAppBlue,
    onPrimary = VendorAppOnPrimary,
    secondary = VendorAppLightBlue,
    onSecondary = VendorAppOnLightBlue,
    tertiary = VendorAppStatusPaid,
    background = VendorAppBackground,
    onBackground = VendorAppTextPrimary,
    surface = VendorAppSurface,
    onSurface = VendorAppTextPrimary,
    surfaceVariant = VendorAppSurfaceVariant,
    onSurfaceVariant = VendorAppTextSecondary,
    secondaryContainer = VendorAppLightBlue,
    onSecondaryContainer = VendorAppDarkBlueText,
    outline = VendorAppDivider,
    outlineVariant = VendorAppChipUnselected
)

private val DarkColorScheme = darkColorScheme(
    primary = VendorAppBlue, 
    onPrimary = VendorAppOnPrimary,
    secondary = VendorAppLightBlue, 
    onSecondary = VendorAppOnLightBlue,
    background = Color(0xFF121212), 
    onBackground = Color(0xFFE0E0E0), 
    surface = Color(0xFF1E1E1E), 
    onSurface = Color(0xFFE0E0E0),
    tertiary = VendorAppStatusPaid,
)

@Composable
fun VendorAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography, 
        shapes = VendorAppShapes,
        content = content
    )
}
