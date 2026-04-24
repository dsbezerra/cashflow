package com.github.dsbezerra.cashflow.ui.screens.transactions

sealed interface TransactionListAction {
    data object Refresh :
        TransactionListAction
    data class DeleteTransaction(val id: String) :
        TransactionListAction
}
