package com.github.dsbezerra.cashflow.data.mapper

import com.github.dsbezerra.cashflow.db.Account as AccountEntity
import com.github.dsbezerra.cashflow.domain.model.Account
import com.github.dsbezerra.cashflow.domain.model.toDecimal

fun AccountEntity.toDomain(): Account = Account(
    id = id,
    name = name,
    type = type,
    currency = currency,
    initialBalance = initialBalance.toDecimal(),
    color = color,
    icon = icon,
    isArchived = isArchived,
    isDefault = isDefault,
    createdAt = createdAt,
)

fun Account.toEntity(): AccountEntity = AccountEntity(
    id = id,
    name = name,
    type = type,
    currency = currency,
    initialBalance = initialBalance.toDouble(),
    color = color,
    icon = icon,
    isArchived = isArchived,
    isDefault = isDefault,
    createdAt = createdAt,
)
