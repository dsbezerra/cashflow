package com.github.dsbezerra.cashflow.ui.transactions

import com.github.dsbezerra.cashflow.domain.model.Transaction

data class TransactionListState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)
