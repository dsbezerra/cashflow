package com.github.dsbezerra.cashflow.data.mapper

import com.github.dsbezerra.cashflow.db.CashTransaction as TransactionEntity
import com.github.dsbezerra.cashflow.domain.model.Transaction

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    type = type,
    amount = amount,
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
    amount = amount,
    description = description,
    date = date,
    notes = notes,
    attachmentPath = attachmentPath,
    isRecurring = isRecurring,
    recurringId = recurringId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
