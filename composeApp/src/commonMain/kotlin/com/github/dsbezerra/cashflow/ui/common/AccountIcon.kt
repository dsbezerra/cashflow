package com.github.dsbezerra.cashflow.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector

fun accountIcon(iconName: String): ImageVector = when (iconName.lowercase()) {
    "account_balance", "checking" -> Icons.Default.AccountBalance
    "savings" -> Icons.Default.Savings
    "payments", "cash" -> Icons.Default.Payments
    "credit_card", "credit" -> Icons.Default.CreditCard
    "trending_up", "investment" -> Icons.Default.TrendingUp
    "attach_money" -> Icons.Default.AttachMoney
    else -> Icons.Default.AccountBalance
}

val accountIconOptions = listOf(
    "account_balance",
    "savings",
    "payments",
    "credit_card",
    "trending_up",
    "attach_money",
)
