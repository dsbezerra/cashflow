package com.github.dsbezerra.cashflow.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CashFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
                content = content,
            )
        }
    }
}
