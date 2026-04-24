package com.github.dsbezerra.cashflow.ui.screens.accounts

sealed interface AccountListEvent {
    data class ShowError(val message: String) :
        com.github.dsbezerra.cashflow.ui.screens.accounts.AccountListEvent
}
