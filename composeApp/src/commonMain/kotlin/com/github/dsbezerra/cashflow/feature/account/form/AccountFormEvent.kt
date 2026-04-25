package com.github.dsbezerra.cashflow.feature.account.form

sealed interface AccountFormEvent {
    data object NavigateBack : AccountFormEvent
    data class ShowError(val message: String) :
        AccountFormEvent
}
