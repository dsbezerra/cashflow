package com.github.dsbezerra.cashflow.core.data.mapper

import com.github.dsbezerra.cashflow.db.Budget as BudgetEntity
import com.github.dsbezerra.cashflow.core.domain.model.Budget
import com.github.dsbezerra.cashflow.core.domain.model.toDecimal

fun BudgetEntity.toDomain(): Budget = Budget(
    id = id,
    categoryId = categoryId,
    amount = amount.toDecimal(),
    period = period,
    startDate = startDate,
    isActive = isActive,
)

fun Budget.toEntity(): BudgetEntity = BudgetEntity(
    id = id,
    categoryId = categoryId,
    amount = amount.toDouble(),
    period = period,
    startDate = startDate,
    isActive = isActive,
)
