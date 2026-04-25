package com.github.dsbezerra.cashflow.feature.account.detail

sealed interface AccountDetailAction {
    data object ConfirmDelete :
        AccountDetailAction
    data object ConfirmArchive :
        AccountDetailAction
}
