package com.github.dsbezerra.cashflow.ui.screens.recurring

import com.github.dsbezerra.cashflow.domain.model.Account
import com.github.dsbezerra.cashflow.domain.model.Category
import com.github.dsbezerra.cashflow.domain.model.Frequency
import com.github.dsbezerra.cashflow.domain.model.TransactionType

data class RecurringRuleFormState(
    val ruleId: String? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val description: String = "",
    val descriptionError: String? = null,
    val amountInput: String = "",
    val amountError: String? = null,
    val type: TransactionType = TransactionType.EXPENSE,
    val selectedAccountId: String? = null,
    val accountError: String? = null,
    val selectedCategoryId: String? = null,
    val categoryError: String? = null,
    val frequency: Frequency = Frequency.MONTHLY,
    val interval: Int = 1,
    val startDate: Long = 0L,
    val endDate: Long? = null,
    val isActive: Boolean = true,
    val availableAccounts: List<Account> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
)
