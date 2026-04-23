package com.github.dsbezerra.cashflow.ui.accounts

sealed interface AccountDetailAction {
    data object ConfirmDelete : AccountDetailAction
    data object ConfirmArchive : AccountDetailAction
}
