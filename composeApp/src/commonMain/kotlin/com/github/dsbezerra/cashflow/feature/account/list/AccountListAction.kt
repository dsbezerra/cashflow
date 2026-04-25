package com.github.dsbezerra.cashflow.feature.account.list

sealed interface AccountListAction {
    data object Refresh : AccountListAction
    data class SetDefault(val accountId: String) : AccountListAction
}
