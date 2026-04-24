package com.github.dsbezerra.cashflow.ui.screens.transactions

sealed interface TransactionDetailEvent {
    data object NavigateBack :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailEvent
    data class ShowError(val message: String) :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailEvent
}
