package com.github.dsbezerra.cashflow.ui.accounts

import com.github.dsbezerra.cashflow.domain.model.Account

data class AccountWithBalance(val account: Account, val balance: Double)

data class AccountListState(
    val accounts: List<AccountWithBalance> = emptyList(),
    val isLoading: Boolean = true,
)
