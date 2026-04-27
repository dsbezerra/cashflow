package com.github.dsbezerra.cashflow.feature.transaction.list

import kotlinx.datetime.LocalDate

sealed interface TransactionListItem {
    data class Header(val date: LocalDate) : TransactionListItem
    data class Entry(val transaction: TransactionUiModel) : TransactionListItem
}
