package com.github.dsbezerra.cashflow.feature.account.list

import com.github.dsbezerra.cashflow.core.domain.model.Account
import com.github.dsbezerra.cashflow.core.domain.model.Decimal

data class AccountWithBalance(val account: Account, val balance: Decimal)

data class AccountListState(
    val accounts: List<AccountWithBalance> = emptyList(),
    val isLoading: Boolean = true,
)
