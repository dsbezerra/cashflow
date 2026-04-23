package com.github.dsbezerra.cashflow.ui.dashboard

import com.github.dsbezerra.cashflow.domain.model.Category
import com.github.dsbezerra.cashflow.domain.model.DashboardSummary

data class DashboardState(
    val isLoading: Boolean = true,
    val summary: DashboardSummary? = null,
    val categories: Map<String, Category> = emptyMap(),
    val error: String? = null,
)
