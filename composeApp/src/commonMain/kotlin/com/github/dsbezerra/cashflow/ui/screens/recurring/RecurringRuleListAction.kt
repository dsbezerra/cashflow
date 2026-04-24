package com.github.dsbezerra.cashflow.ui.screens.recurring

sealed interface RecurringRuleListAction {
    data class ToggleActive(val id: String, val isActive: Boolean) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListAction
    data class Delete(val id: String) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListAction
}
