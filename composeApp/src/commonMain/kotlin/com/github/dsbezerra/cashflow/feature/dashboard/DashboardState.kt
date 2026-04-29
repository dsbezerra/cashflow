package com.github.dsbezerra.cashflow.feature.dashboard

import com.github.dsbezerra.cashflow.core.domain.model.Account
import com.github.dsbezerra.cashflow.core.domain.model.Category
import com.github.dsbezerra.cashflow.core.domain.model.DashboardSummary

data class DashboardState(
    val isLoading: Boolean = true,
    val summary: DashboardSummary? = null,
    val categories: Map<String, Category> = emptyMap(),
    val accounts: List<Account> = emptyList(),
    val selectedAccountId: String? = null,
    val selectedYear: Int = 0,
    val selectedMonth: Int = 0,
    val topExpenseCategories: List<CategoryExpense> = emptyList(),
    val error: String? = null,
)
