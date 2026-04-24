package com.github.dsbezerra.cashflow.ui.screens.transactions

import com.github.dsbezerra.cashflow.domain.model.TransactionType

sealed interface TransactionDetailAction {
    data class TypeChanged(val type: TransactionType) :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction
    data class AmountChanged(val amount: String) :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction
    data class DescriptionChanged(val description: String) :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction
    data class DateChanged(val epochMillis: Long) :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction
    data class CategorySelected(val categoryId: String) :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction
    data class AccountSelected(val accountId: String) :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction
    data class ToAccountSelected(val accountId: String) :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction
    data class NotesChanged(val notes: String) :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction
    data object Save : com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction
    data object Delete :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction
    data object ConfirmDelete :
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction
}
