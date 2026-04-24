package com.github.dsbezerra.cashflow.ui.screens.recurring

import com.github.dsbezerra.cashflow.domain.model.Frequency
import com.github.dsbezerra.cashflow.domain.model.TransactionType

sealed interface RecurringRuleFormAction {
    data class DescriptionChanged(val description: String) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction
    data class AmountChanged(val amount: String) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction
    data class TypeChanged(val type: TransactionType) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction
    data class AccountSelected(val accountId: String) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction
    data class CategorySelected(val categoryId: String) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction
    data class FrequencyChanged(val frequency: Frequency) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction
    data class IntervalChanged(val interval: Int) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction
    data class StartDateChanged(val epochMillis: Long) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction
    data class EndDateChanged(val epochMillis: Long?) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction
    data class ActiveChanged(val isActive: Boolean) :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction
    data object Save : com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction
    data object ConfirmDelete :
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction
}
