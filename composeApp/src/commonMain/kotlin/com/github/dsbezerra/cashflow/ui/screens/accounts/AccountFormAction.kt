package com.github.dsbezerra.cashflow.ui.screens.accounts

import com.github.dsbezerra.cashflow.domain.model.AccountType

sealed interface AccountFormAction {
    data class NameChanged(val name: String) :
        AccountFormAction
    data class TypeChanged(val type: AccountType) :
        AccountFormAction
    data class CurrencyChanged(val currency: String) :
        AccountFormAction
    data class InitialBalanceChanged(val cents: Long) :
        AccountFormAction
    data class ColorChanged(val color: String) :
        AccountFormAction
    data class IconChanged(val icon: String) :
        AccountFormAction
    data object Save : AccountFormAction
    data object ConfirmDelete : AccountFormAction
}
