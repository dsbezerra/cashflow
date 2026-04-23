package com.github.dsbezerra.cashflow.ui.accounts

sealed interface AccountListEvent {
    data class ShowError(val message: String) : AccountListEvent
}
