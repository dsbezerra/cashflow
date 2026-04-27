package com.github.dsbezerra.cashflow.feature.transaction.list

import com.github.dsbezerra.cashflow.core.domain.model.Decimal
import com.github.dsbezerra.cashflow.core.domain.model.Transaction
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType

data class TransactionUiModel(
    val id: String,
    val description: String,
    val categoryName: String,
    val amount: Decimal,
    val type: TransactionType,
    val date: Long,
)

fun Transaction.toUiModel(categoryMap: Map<String, String>) = TransactionUiModel(
    id = id,
    description = description,
    categoryName = categoryMap[categoryId].orEmpty(),
    amount = amount,
    type = type,
    date = date,
)
