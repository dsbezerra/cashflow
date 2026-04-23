package com.github.dsbezerra.cashflow.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.github.dsbezerra.cashflow.domain.model.TransactionType

@Composable
fun AmountText(
    amount: Double,
    type: TransactionType,
    modifier: Modifier = Modifier,
) {
    val (color, prefix) = when (type) {
        TransactionType.INCOME -> Color(0xFF4CAF50) to "+"
        TransactionType.EXPENSE -> Color(0xFFF44336) to "-"
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.onSurface to ""
    }
    Text(
        text = "$prefix$${"%.2f".format(amount)}",
        color = color,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier,
    )
}
