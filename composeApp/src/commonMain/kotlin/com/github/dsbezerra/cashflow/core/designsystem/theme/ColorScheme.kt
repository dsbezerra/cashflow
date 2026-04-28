package com.github.dsbezerra.cashflow.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
expect fun rememberColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme
