package com.github.dsbezerra.cashflow.feature.transaction.list

import com.github.dsbezerra.cashflow.core.domain.model.Transaction

data class TransactionListState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)
