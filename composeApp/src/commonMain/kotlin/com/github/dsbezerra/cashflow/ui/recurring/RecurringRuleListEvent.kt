package com.github.dsbezerra.cashflow.ui.recurring

sealed interface RecurringRuleListEvent {
    data class ShowError(val message: String) : RecurringRuleListEvent
}
