package com.github.dsbezerra.cashflow.feature.account.list

sealed interface AccountListEvent {
    data class ShowError(val message: String) :
        AccountListEvent
}
