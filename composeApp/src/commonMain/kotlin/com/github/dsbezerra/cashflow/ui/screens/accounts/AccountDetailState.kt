package com.github.dsbezerra.cashflow.ui.screens.accounts

import com.github.dsbezerra.cashflow.domain.model.Account
import com.github.dsbezerra.cashflow.domain.model.Transaction

data class AccountDetailState(
    val account: Account? = null,
    val balance: Double = 0.0,
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
)
