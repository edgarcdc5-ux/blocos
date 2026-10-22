package com.cleaneditor.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class AppTheme(val label: String) { SUNSET("Azul moderno (padrão)"), CLARO("Claro") }

@Immutable
data class Palette(
    val isDark: Boolean,
    val background: Color,
    val glow1: Color,
    val glow2: Color,
    val surface: Color,
    val surfaceStrong: Color,
    val border: Color,
    val text: Color,
    val muted: Color,
    val accent: Color,
    val accentText: Color,
    val gradient: List<Color>,
    val high: Color,
    val medium: Color,
    val low: Color,
    val codeHeading: Color,
    val codeBullet: Color,
    val codeString: Color,
    val codeNumber: Color,
) {
    val brush: Brush get() = Brush.linearGradient(gradient)
}

val SunsetPalette = Palette(
    isDark = true,
    background = Color(0xFF0C1125),
    glow1 = Color(0x553B82F6),
    glow2 = Color(0x445B4BFF),
    surface = Color(0x201D2B50),
    surfaceStrong = Color(0xFF202D4D),
    border = Color(0x335E77AD),
    text = Color(0xFFF5F7FF),
    muted = Color(0xFF9AA9C8),
    accent = Color(0xFF346BFA),
    accentText = Color.White,
    gradient = listOf(Color(0xFF3B82F6), Color(0xFF7953F7)),
    high = Color(0xFFFB7185),
    medium = Color(0xFFFBBF24),
    low = Color(0xFF34D399),
    codeHeading = Color(0xFF93C5FD),
    codeBullet = Color(0xFFB8A5FF),
    codeString = Color(0xFFE9BAFF),
    codeNumber = Color(0xFFC4B5FD),
)

val ClaroPalette = Palette(
    isDark = false,
    background = Color(0xFFF7F9FF),
    glow1 = Color(0x333B82F6),
    glow2 = Color(0x227953F7),
    surface = Color(0xF2FFFFFF),
    surfaceStrong = Color(0xFFFFFFFF),
    border = Color(0xFFE1E7F3),
    text = Color(0xFF18263E),
    muted = Color(0xFF62748F),
    accent = Color(0xFF346BFA),
    accentText = Color.White,
    gradient = listOf(Color(0xFF3B82F6), Color(0xFF7953F7)),
    high = Color(0xFFF43F5E),
    medium = Color(0xFFF59E0B),
    low = Color(0xFF10B981),
    codeHeading = Color(0xFF2557CE),
    codeBullet = Color(0xFF6D4FD4),
    codeString = Color(0xFF9A3BD0),
    codeNumber = Color(0xFF7C3AED),
)

val LocalPalette = staticCompositionLocalOf { SunsetPalette }

@Composable
fun CleanEditorTheme(theme: AppTheme, content: @Composable () -> Unit) {
    val p = if (theme == AppTheme.CLARO) ClaroPalette else SunsetPalette
    val scheme = if (p.isDark) darkColorScheme(
        primary = p.accent, onPrimary = p.accentText, background = p.background,
        surface = Color(0xFF151D36), onSurface = p.text, onBackground = p.text,
        surfaceVariant = Color(0xFF222C47), onSurfaceVariant = p.muted,
    ) else lightColorScheme(
        primary = p.accent, onPrimary = p.accentText, background = p.background,
        surface = Color.White, onSurface = p.text, onBackground = p.text,
        surfaceVariant = Color(0xFFF1F5FF), onSurfaceVariant = p.muted,
    )
    CompositionLocalProvider(LocalPalette provides p) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
