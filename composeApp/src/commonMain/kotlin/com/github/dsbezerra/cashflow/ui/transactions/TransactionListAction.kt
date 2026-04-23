package com.github.dsbezerra.cashflow.ui.transactions

sealed interface TransactionListAction {
    data object Refresh : TransactionListAction
    data class DeleteTransaction(val id: String) : TransactionListAction
}
