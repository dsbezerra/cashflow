package com.github.dsbezerra.cashflow.domain.model

data class DashboardSummary(
    val accountBalances: List<Pair<Account, Double>>,
    val totalBalance: Double,
    val monthlyIncome: Double,
    val monthlyExpenses: Double,
    val netBalance: Double,
    val recentTransactions: List<Transaction>,
    val last6MonthsBreakdown: List<MonthlyAmount>,
)
