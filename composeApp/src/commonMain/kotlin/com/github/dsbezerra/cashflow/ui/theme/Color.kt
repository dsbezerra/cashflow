package com.github.dsbezerra.cashflow.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── Seed: deep finance green #1A6B4A ─────────────────────────────────────────

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006C47),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF8EF8C3),
    onPrimaryContainer = Color(0xFF002113),
    secondary = Color(0xFF4D6357),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCFE9D9),
    onSecondaryContainer = Color(0xFF0A1F16),
    tertiary = Color(0xFF3D6372),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC1E8FA),
    onTertiaryContainer = Color(0xFF001F28),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFBFDF9),
    onBackground = Color(0xFF191C1A),
    surface = Color(0xFFFBFDF9),
    onSurface = Color(0xFF191C1A),
    surfaceVariant = Color(0xFFDCE5DC),
    onSurfaceVariant = Color(0xFF404943),
    outline = Color(0xFF707972),
    outlineVariant = Color(0xFFC0C9C0),
    inverseSurface = Color(0xFF2D312E),
    inverseOnSurface = Color(0xFFEFF1ED),
    inversePrimary = Color(0xFF71DB9F),
    surfaceContainerLowest = Color(0xFFF5F8F4),
    surfaceContainerLow = Color(0xFFEFF2EE),
    surfaceContainer = Color(0xFFE9ECE8),
    surfaceContainerHigh = Color(0xFFE3E6E2),
    surfaceContainerHighest = Color(0xFFDDE0DC),
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF71DB9F),
    onPrimary = Color(0xFF00391F),
    primaryContainer = Color(0xFF005234),
    onPrimaryContainer = Color(0xFF8EF8C3),
    secondary = Color(0xFFB3CCBD),
    onSecondary = Color(0xFF1F352A),
    secondaryContainer = Color(0xFF354B40),
    onSecondaryContainer = Color(0xFFCFE9D9),
    tertiary = Color(0xFFA5CCDE),
    onTertiary = Color(0xFF073544),
    tertiaryContainer = Color(0xFF254B5A),
    onTertiaryContainer = Color(0xFFC1E8FA),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF191C1A),
    onBackground = Color(0xFFE1E3DF),
    surface = Color(0xFF191C1A),
    onSurface = Color(0xFFE1E3DF),
    surfaceVariant = Color(0xFF404943),
    onSurfaceVariant = Color(0xFFC0C9C0),
    outline = Color(0xFF8A938C),
    outlineVariant = Color(0xFF404943),
    inverseSurface = Color(0xFFE1E3DF),
    inverseOnSurface = Color(0xFF2D312E),
    inversePrimary = Color(0xFF006C47),
    surfaceContainerLowest = Color(0xFF141715),
    surfaceContainerLow = Color(0xFF191C1A),
    surfaceContainer = Color(0xFF1D201E),
    surfaceContainerHigh = Color(0xFF272B28),
    surfaceContainerHighest = Color(0xFF323533),
)

// ── Finance semantic colors ───────────────────────────────────────────────────

/** Income positive amounts — light mode (dark green, readable on white) */
val IncomeGreen = Color(0xFF2E7D32)

/** Income positive amounts — dark mode (soft green, readable on dark surface) */
val IncomeGreenDark = Color(0xFF81C784)

/** Expense / negative amounts — light mode (dark red, readable on white) */
val ExpenseRed = Color(0xFFC62828)

/** Expense / negative amounts — dark mode (soft red, readable on dark surface) */
val ExpenseRedDark = Color(0xFFEF9A9A)
