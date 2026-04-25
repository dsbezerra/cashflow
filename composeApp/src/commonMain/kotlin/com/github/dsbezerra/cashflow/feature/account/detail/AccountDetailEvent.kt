package com.github.dsbezerra.cashflow.feature.account.detail

sealed interface AccountDetailEvent {
    data object NavigateBack : AccountDetailEvent
    data class NavigateToEdit(val accountId: String) :
        AccountDetailEvent
    data class ShowError(val message: String) :
        AccountDetailEvent
}
