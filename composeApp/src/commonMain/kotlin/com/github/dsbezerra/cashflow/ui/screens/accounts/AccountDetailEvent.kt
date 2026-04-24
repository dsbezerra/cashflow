package com.github.dsbezerra.cashflow.ui.screens.accounts

sealed interface AccountDetailEvent {
    data object NavigateBack : AccountDetailEvent
    data class NavigateToEdit(val accountId: String) :
        AccountDetailEvent
    data class ShowError(val message: String) :
        AccountDetailEvent
}
