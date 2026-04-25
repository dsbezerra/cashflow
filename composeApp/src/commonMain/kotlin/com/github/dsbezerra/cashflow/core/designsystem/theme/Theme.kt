package com.github.dsbezerra.cashflow.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CashFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = rememberColorScheme(darkTheme = darkTheme, dynamicColor = dynamicColor)
    val cashFlowColors = if (darkTheme) darkCashFlowColors else lightCashFlowColors

    val motionScheme = MotionScheme.expressive()

    CompositionLocalProvider(LocalCashFlowColors provides cashFlowColors) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = motionScheme,
            shapes = Shapes(),
            typography = CashFlowTypography,
            content = content,
        )
    }
}
