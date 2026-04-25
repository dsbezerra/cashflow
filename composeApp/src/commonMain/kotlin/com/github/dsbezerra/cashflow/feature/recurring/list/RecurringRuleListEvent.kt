package com.github.dsbezerra.cashflow.feature.recurring.list

sealed interface RecurringRuleListEvent {
    data class ShowError(val message: String) :
        RecurringRuleListEvent
}
