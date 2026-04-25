package com.github.dsbezerra.cashflow.feature.transaction.list

sealed interface TransactionListAction {
    data object Refresh :
        TransactionListAction
    data class DeleteTransaction(val id: String) :
        TransactionListAction
}
