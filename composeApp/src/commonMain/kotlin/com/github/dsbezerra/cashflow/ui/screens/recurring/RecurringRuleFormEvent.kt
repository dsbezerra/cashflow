package com.github.dsbezerra.cashflow.ui.screens.recurring

sealed interface RecurringRuleFormEvent {
    data object NavigateBack :
        RecurringRuleFormEvent
    data class ShowError(val message: String) :
        RecurringRuleFormEvent
}
