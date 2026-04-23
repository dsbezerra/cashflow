package com.github.dsbezerra.cashflow.ui.transactions

import com.github.dsbezerra.cashflow.domain.model.TransactionType

sealed interface TransactionDetailAction {
    data class TypeChanged(val type: TransactionType) : TransactionDetailAction
    data class AmountChanged(val amount: String) : TransactionDetailAction
    data class DescriptionChanged(val description: String) : TransactionDetailAction
    data class DateChanged(val epochMillis: Long) : TransactionDetailAction
    data class CategorySelected(val categoryId: String) : TransactionDetailAction
    data class AccountSelected(val accountId: String) : TransactionDetailAction
    data class ToAccountSelected(val accountId: String) : TransactionDetailAction
    data class NotesChanged(val notes: String) : TransactionDetailAction
    data object Save : TransactionDetailAction
    data object Delete : TransactionDetailAction
    data object ConfirmDelete : TransactionDetailAction
}
