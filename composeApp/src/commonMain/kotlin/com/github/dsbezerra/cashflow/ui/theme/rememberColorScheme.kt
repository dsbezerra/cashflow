package com.github.dsbezerra.cashflow.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
expect fun rememberColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme
