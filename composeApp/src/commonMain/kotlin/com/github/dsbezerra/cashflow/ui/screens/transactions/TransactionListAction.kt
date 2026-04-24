package com.github.dsbezerra.cashflow.ui.screens.transactions

sealed interface TransactionListAction {
    data object Refresh :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionListAction
    data class DeleteTransaction(val id: String) :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionListAction
}
