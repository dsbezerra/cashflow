package com.github.dsbezerra.cashflow.ui.screens.transactions

import com.github.dsbezerra.cashflow.domain.model.Account
import com.github.dsbezerra.cashflow.domain.model.Category
import com.github.dsbezerra.cashflow.domain.model.TransactionType
import kotlin.time.Clock

data class TransactionDetailState(
    val transactionId: String? = null,
    val type: TransactionType = TransactionType.EXPENSE,
    val amountInput: String = "",
    val description: String = "",
    val selectedDate: Long = Clock.System.now().toEpochMilliseconds(),
    val selectedCategoryId: String? = null,
    val selectedAccountId: String? = null,
    val selectedToAccountId: String? = null,
    val notes: String = "",
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val amountError: String? = null,
    val descriptionError: String? = null,
    val categoryError: String? = null,
    val accountError: String? = null,
)
