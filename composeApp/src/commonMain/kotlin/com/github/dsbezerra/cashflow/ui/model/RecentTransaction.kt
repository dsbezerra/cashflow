package com.github.dsbezerra.cashflow.ui.model

import com.github.dsbezerra.cashflow.domain.model.Category
import com.github.dsbezerra.cashflow.domain.model.Transaction
import com.github.dsbezerra.cashflow.domain.model.TransactionType
import com.github.dsbezerra.cashflow.util.formatLongDate

data class RecentTransaction(
    val id: String,
    val type: TransactionType,
    val amount: String,
    val description: String,
    val category: String,
    val dateFormatted: String,
)

fun Transaction.toRecentTransaction() = RecentTransaction(
    id = id,
    type = type,
    amount = amount.toCurrency(),
    description = description,
    category = categoryId,
    dateFormatted = formatLongDate(date, "dd 'de' MMMM"),
)

fun List<RecentTransaction>.mapCategories(categories: List<Category>) = map {
    it.copy(category = categories.find { c -> c.id == it.category }?.name.orEmpty())
}