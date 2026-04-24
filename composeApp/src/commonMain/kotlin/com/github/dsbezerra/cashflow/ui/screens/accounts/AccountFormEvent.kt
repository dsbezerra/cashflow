package com.github.dsbezerra.cashflow.ui.screens.accounts

sealed interface AccountFormEvent {
    data object NavigateBack : AccountFormEvent
    data class ShowError(val message: String) :
        AccountFormEvent
}
