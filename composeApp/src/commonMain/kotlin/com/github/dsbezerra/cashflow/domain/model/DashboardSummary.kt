package com.github.dsbezerra.cashflow.domain.model

import com.github.dsbezerra.cashflow.ui.model.RecentTransaction

data class DashboardSummary(
    val accountBalances: List<Pair<Account, Decimal>>,
    val monthName: String,
    val year: Int,
    val transactionCount: Int,
    val totalBalance: Decimal,
    val monthlyIncome: Decimal,
    val monthlyExpenses: Decimal,
    val netBalance: Decimal,
    val recentTransactions: List<RecentTransaction>,
    val last6MonthsBreakdown: List<MonthlyAmount>,
)
