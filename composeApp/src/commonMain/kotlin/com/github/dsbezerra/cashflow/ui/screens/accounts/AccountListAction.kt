package com.github.dsbezerra.cashflow.ui.screens.accounts

sealed interface AccountListAction {
    data object Refresh : AccountListAction
    data class SetDefault(val accountId: String) : AccountListAction
}
