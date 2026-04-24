package com.github.dsbezerra.cashflow.ui.recurring

import com.github.dsbezerra.cashflow.domain.model.RecurringRule

data class RecurringRuleListState(
    val isLoading: Boolean = true,
    val rules: List<RecurringRule> = emptyList(),
)
