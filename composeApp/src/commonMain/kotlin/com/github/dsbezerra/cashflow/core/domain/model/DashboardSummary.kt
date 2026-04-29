package com.github.dsbezerra.cashflow.core.domain.model

import com.github.dsbezerra.cashflow.feature.dashboard.RecentTransaction

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
    val expensesByCategoryId: Map<String, Double> = emptyMap(),
)
