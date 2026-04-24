package com.github.dsbezerra.cashflow.ui.screens.recurring

sealed interface RecurringRuleFormEvent {
    data object NavigateBack :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormEvent
    data class ShowError(val message: String) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormEvent
}
