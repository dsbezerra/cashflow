package com.github.dsbezerra.cashflow.feature.transaction.list

import com.github.dsbezerra.cashflow.core.domain.model.TransactionType

sealed interface TransactionListAction {
    data class DeleteTransaction(val id: String) : TransactionListAction
    data class SearchQueryChanged(val query: String) : TransactionListAction
    data class ApplyFilters(
        val type: TransactionType?,
        val period: TransactionListPeriod?,
        val categoryId: String?,
    ) : TransactionListAction
}
