package com.github.dsbezerra.cashflow.ui.accounts

import com.github.dsbezerra.cashflow.domain.model.AccountType

data class AccountFormState(
    val accountId: String? = null,
    val name: String = "",
    val nameError: String? = null,
    val type: AccountType = AccountType.CHECKING,
    val currency: String = "BRL",
    val initialBalanceInput: String = "0",
    val initialBalanceError: String? = null,
    val color: String = "#4CAF50",
    val icon: String = "account_balance",
    val isArchived: Boolean = false,
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
)
