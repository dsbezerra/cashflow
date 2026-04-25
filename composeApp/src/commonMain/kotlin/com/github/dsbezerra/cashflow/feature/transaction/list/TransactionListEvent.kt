package com.github.dsbezerra.cashflow.feature.transaction.list

sealed interface TransactionListEvent {
    data class ShowError(val message: String) :
        TransactionListEvent
}
