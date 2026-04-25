package com.devgardenaj.thisday.infra

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Peach100 = Color(0xFFFFF8F5)
private val Peach200 = Color(0xFFFFEDE5)
private val Peach300 = Color(0xFFFFD5C5)
private val Peach400 = Color(0xFFE8A990)
private val Peach500 = Color(0xFFD4856A)
private val Peach600 = Color(0xFFB86B52)
private val Warm800 = Color(0xFF3D1A0E)
private val WarmGrey = Color(0xFF6B4A40)
private val WarmGreyLight = Color(0xFFEDD5CC)
private val WarmBrown = Color(0xFF9C6B5A)
private val WarmBrownContainer = Color(0xFFFFDDD0)
private val WarmTaupe = Color(0xFF8C7C5A)
private val WarmTaupeContainer = Color(0xFFFAEEC8)

val PeachColorScheme = lightColorScheme(
    primary = Peach500,
    onPrimary = Color.White,
    primaryContainer = Peach300,
    onPrimaryContainer = Warm800,
    secondary = WarmBrown,
    onSecondary = Color.White,
    secondaryContainer = WarmBrownContainer,
    onSecondaryContainer = Warm800,
    tertiary = WarmTaupe,
    onTertiary = Color.White,
    tertiaryContainer = WarmTaupeContainer,
    onTertiaryContainer = Warm800,
    background = Peach100,
    onBackground = Warm800,
    surface = Peach100,
    onSurface = Warm800,
    surfaceVariant = Peach200,
    onSurfaceVariant = WarmGrey,
    outline = Peach400,
    outlineVariant = WarmGreyLight,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

@Composable
fun ThisDayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PeachColorScheme,
        content = content
    )
}
