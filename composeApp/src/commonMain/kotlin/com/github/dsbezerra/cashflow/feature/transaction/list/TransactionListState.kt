package com.github.dsbezerra.cashflow.feature.transaction.list

import com.github.dsbezerra.cashflow.core.domain.model.Category

data class TransactionListState(
    val categories: List<Category> = emptyList(),
)
