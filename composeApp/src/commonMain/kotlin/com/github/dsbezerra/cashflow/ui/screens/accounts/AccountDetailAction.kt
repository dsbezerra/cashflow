package com.github.dsbezerra.cashflow.ui.screens.accounts

sealed interface AccountDetailAction {
    data object ConfirmDelete :
        AccountDetailAction
    data object ConfirmArchive :
        AccountDetailAction
}
