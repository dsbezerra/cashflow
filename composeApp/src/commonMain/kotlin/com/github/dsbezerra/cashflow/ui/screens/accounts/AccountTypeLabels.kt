package com.github.dsbezerra.cashflow.ui.screens.accounts

import com.github.dsbezerra.cashflow.domain.model.AccountType

fun accountTypeName(type: AccountType): String = when (type) {
    AccountType.CHECKING -> "Conta Corrente"
    AccountType.SAVINGS -> "Poupança"
    AccountType.CASH -> "Dinheiro"
    AccountType.CREDIT -> "Cartão de Crédito"
    AccountType.INVESTMENT -> "Investimentos"
}
