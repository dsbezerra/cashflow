package com.github.dsbezerra.cashflow.feature.transaction.detail

sealed interface TransactionDetailEvent {
    data object NavigateBack :
        TransactionDetailEvent
    data class ShowError(val message: String) :
        TransactionDetailEvent
}
