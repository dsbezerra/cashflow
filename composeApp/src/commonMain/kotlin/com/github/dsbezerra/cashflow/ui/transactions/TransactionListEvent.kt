package com.github.dsbezerra.cashflow.ui.transactions

sealed interface TransactionListEvent {
    data class ShowError(val message: String) : TransactionListEvent
}
