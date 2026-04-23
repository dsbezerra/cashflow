package com.github.dsbezerra.cashflow.ui.accounts

sealed interface AccountListAction {
    data object Refresh : AccountListAction
}
