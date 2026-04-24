package com.github.dsbezerra.cashflow.ui.recurring

sealed interface RecurringRuleListAction {
    data class ToggleActive(val id: String, val isActive: Boolean) : RecurringRuleListAction
    data class Delete(val id: String) : RecurringRuleListAction
}
