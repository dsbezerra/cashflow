package com.github.dsbezerra.cashflow.ui.screens.transactions

sealed interface TransactionListEvent {
    data class ShowError(val message: String) :
        TransactionListEvent
}
