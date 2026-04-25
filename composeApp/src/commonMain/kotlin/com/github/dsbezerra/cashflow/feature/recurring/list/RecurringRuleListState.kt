package com.github.dsbezerra.cashflow.feature.recurring.list

import com.github.dsbezerra.cashflow.core.domain.model.RecurringRule

data class RecurringRuleListState(
    val isLoading: Boolean = true,
    val rules: List<RecurringRule> = emptyList(),
)
