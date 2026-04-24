package com.github.dsbezerra.cashflow.ui.screens.accounts

sealed interface AccountDetailAction {
    data object ConfirmDelete :
        com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailAction
    data object ConfirmArchive :
        com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailAction
}
