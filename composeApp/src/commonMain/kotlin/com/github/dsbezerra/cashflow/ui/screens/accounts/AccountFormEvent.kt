package com.github.dsbezerra.cashflow.ui.screens.accounts

sealed interface AccountFormEvent {
    data object NavigateBack : com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormEvent
    data class ShowError(val message: String) :
        com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormEvent
}
