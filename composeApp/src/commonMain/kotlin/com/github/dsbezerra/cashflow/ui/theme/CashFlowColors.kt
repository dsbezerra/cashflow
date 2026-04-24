package com.github.dsbezerra.cashflow.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CashFlowColors(
    val income: Color,
    val expense: Color,
)

val lightCashFlowColors = CashFlowColors(income = IncomeGreen, expense = ExpenseRed)
val darkCashFlowColors = CashFlowColors(income = IncomeGreenDark, expense = ExpenseRedDark)

val LocalCashFlowColors = staticCompositionLocalOf { lightCashFlowColors }

object AppColors {
    val colors: CashFlowColors
        @Composable get() = LocalCashFlowColors.current
}
