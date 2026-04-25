package com.github.dsbezerra.cashflow.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
import com.github.dsbezerra.cashflow.core.designsystem.theme.AppColors

@Composable
fun AmountText(
    amount: String,
    type: TransactionType,
    modifier: Modifier = Modifier,
) {
    val cashFlowColors = AppColors.colors
    val (color, prefix) = when (type) {
        TransactionType.INCOME -> cashFlowColors.income to "+"
        TransactionType.EXPENSE -> cashFlowColors.expense to "-"
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.onSurface to ""
    }
    Text(
        text = "$prefix$amount",
        color = color,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier,
    )
}
