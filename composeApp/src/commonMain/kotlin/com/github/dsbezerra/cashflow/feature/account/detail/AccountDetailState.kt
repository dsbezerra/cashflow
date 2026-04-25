package com.github.dsbezerra.cashflow.feature.account.detail

import com.github.dsbezerra.cashflow.core.domain.model.Account
import com.github.dsbezerra.cashflow.core.domain.model.Transaction

data class AccountDetailState(
    val account: Account? = null,
    val balance: Double = 0.0,
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
)
