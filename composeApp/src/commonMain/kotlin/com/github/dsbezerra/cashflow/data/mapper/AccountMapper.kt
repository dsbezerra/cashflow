package com.github.dsbezerra.cashflow.data.mapper

import com.github.dsbezerra.cashflow.db.Account as AccountEntity
import com.github.dsbezerra.cashflow.domain.model.Account

fun AccountEntity.toDomain(): Account = Account(
    id = id,
    name = name,
    type = type,
    currency = currency,
    initialBalance = initialBalance,
    color = color,
    icon = icon,
    isArchived = isArchived,
    createdAt = createdAt,
)

fun Account.toEntity(): AccountEntity = AccountEntity(
    id = id,
    name = name,
    type = type,
    currency = currency,
    initialBalance = initialBalance,
    color = color,
    icon = icon,
    isArchived = isArchived,
    createdAt = createdAt,
)
