package com.github.dsbezerra.cashflow.ui.screens.recurring

sealed interface RecurringRuleListEvent {
    data class ShowError(val message: String) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListEvent
}
