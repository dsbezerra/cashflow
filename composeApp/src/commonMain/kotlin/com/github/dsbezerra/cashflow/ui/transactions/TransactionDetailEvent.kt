package com.github.dsbezerra.cashflow.ui.transactions

sealed interface TransactionDetailEvent {
    data object NavigateBack : TransactionDetailEvent
    data class ShowError(val message: String) : TransactionDetailEvent
}
