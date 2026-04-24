package com.github.dsbezerra.cashflow.ui.screens.accounts

sealed interface AccountDetailEvent {
    data object NavigateBack : com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailEvent
    data class NavigateToEdit(val accountId: String) :
        com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailEvent
    data class ShowError(val message: String) :
        com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailEvent
}
