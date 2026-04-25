package com.github.dsbezerra.cashflow.core.data.mapper

import com.github.dsbezerra.cashflow.db.CashTransaction as TransactionEntity
import com.github.dsbezerra.cashflow.core.domain.model.Transaction
import com.github.dsbezerra.cashflow.core.domain.model.toDecimal

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    type = type,
    amount = amount.toDecimal(),
    description = description,
    date = date,
    notes = notes,
    attachmentPath = attachmentPath,
    isRecurring = isRecurring,
    recurringId = recurringId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    type = type,
    amount = amount.toDouble(),
    description = description,
    date = date,
    notes = notes,
    attachmentPath = attachmentPath,
    isRecurring = isRecurring,
    recurringId = recurringId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
