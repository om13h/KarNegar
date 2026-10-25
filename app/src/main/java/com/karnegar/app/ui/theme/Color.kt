package com.karnegar.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.karnegar.app.data.TaskCategory
import com.karnegar.app.data.TaskPriority

val LightColors = lightColorScheme(
    primary = Color(0xFF6A4DE8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE7DEFF),
    onPrimaryContainer = Color(0xFF1F1049),
    secondary = Color(0xFF5B5A72),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE0E0F5),
    onSecondaryContainer = Color(0xFF181A2E),
    tertiary = Color(0xFF77536D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD7F1),
    onTertiaryContainer = Color(0xFF2D1228),
    background = Color(0xFFFBF9FE),
    onBackground = Color(0xFF1B1B21),
    surface = Color(0xFFFBF9FE),
    onSurface = Color(0xFF1B1B21),
    surfaceVariant = Color(0xFFE5E1EF),
    onSurfaceVariant = Color(0xFF47464F),
    outline = Color(0xFF77767F),
    outlineVariant = Color(0xFFC7C5D0),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFCBBEFF),
    onPrimary = Color(0xFF2A1073),
    primaryContainer = Color(0xFF4B2FA8),
    onPrimaryContainer = Color(0xFFE7DEFF),
    secondary = Color(0xFFC3C2D8),
    onSecondary = Color(0xFF2D2F42),
    secondaryContainer = Color(0xFF434559),
    onSecondaryContainer = Color(0xFFE0E0F5),
    tertiary = Color(0xFFE6BAD7),
    onTertiary = Color(0xFF44263D),
    tertiaryContainer = Color(0xFF5D3C55),
    onTertiaryContainer = Color(0xFFFFD7F1),
    background = Color(0xFF131218),
    onBackground = Color(0xFFE5E1E9),
    surface = Color(0xFF131218),
    onSurface = Color(0xFFE5E1E9),
    surfaceVariant = Color(0xFF47464F),
    onSurfaceVariant = Color(0xFFC8C5D0),
    outline = Color(0xFF918F9A),
    outlineVariant = Color(0xFF47464F),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

val CategoryColors: Map<TaskCategory, Color> = mapOf(
    TaskCategory.PERSONAL to Color(0xFF4C8DF6),
    TaskCategory.WORK to Color(0xFF6A4DE8),
    TaskCategory.SHOPPING to Color(0xFF2FA968),
    TaskCategory.HEALTH to Color(0xFFE85A4F),
    TaskCategory.OTHER to Color(0xFF8A8A9E)
)

val PriorityColors: Map<TaskPriority, Color> = mapOf(
    TaskPriority.HIGH to Color(0xFFE5484D),
    TaskPriority.MEDIUM to Color(0xFFF5A524),
    TaskPriority.LOW to Color(0xFF6E6E80)
)
