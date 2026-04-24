package com.github.dsbezerra.cashflow.ui.screens.recurring

import com.github.dsbezerra.cashflow.domain.model.Frequency
import com.github.dsbezerra.cashflow.domain.model.TransactionType

sealed interface RecurringRuleFormAction {
    data class DescriptionChanged(val description: String) : RecurringRuleFormAction
    data class AmountChanged(val amount: String) : RecurringRuleFormAction
    data class TypeChanged(val type: TransactionType) : RecurringRuleFormAction
    data class AccountSelected(val accountId: String) : RecurringRuleFormAction
    data class CategorySelected(val categoryId: String) : RecurringRuleFormAction
    data class FrequencyChanged(val frequency: Frequency) : RecurringRuleFormAction
    data class IntervalChanged(val interval: Int) : RecurringRuleFormAction
    data class StartDateChanged(val epochMillis: Long) : RecurringRuleFormAction
    data class EndDateChanged(val epochMillis: Long?) : RecurringRuleFormAction
    data class ActiveChanged(val isActive: Boolean) : RecurringRuleFormAction
    data object Save : RecurringRuleFormAction
    data object ConfirmDelete : RecurringRuleFormAction
}
