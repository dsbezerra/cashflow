package com.github.dsbezerra.cashflow.ui.screens.accounts

import com.github.dsbezerra.cashflow.domain.model.Account
import com.github.dsbezerra.cashflow.domain.model.Decimal

data class AccountWithBalance(val account: Account, val balance: Decimal)

data class AccountListState(
    val accounts: List<AccountWithBalance> = emptyList(),
    val isLoading: Boolean = true,
)
