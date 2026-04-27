package com.github.dsbezerra.cashflow.feature.account.form

import com.github.dsbezerra.cashflow.core.domain.model.AccountType

data class AccountFormState(
    val accountId: String? = null,
    val name: String = "",
    val nameError: String? = null,
    val type: AccountType = AccountType.CHECKING,
    val currency: String = "BRL",
    val initialBalanceInCents: Long = 0L,
    val initialBalanceError: String? = null,
    val icon: String = "account_balance",
    val isArchived: Boolean = false,
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
)
