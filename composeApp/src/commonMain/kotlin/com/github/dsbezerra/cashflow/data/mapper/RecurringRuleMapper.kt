package com.github.dsbezerra.cashflow.data.mapper

import com.github.dsbezerra.cashflow.db.RecurringRule as RecurringRuleEntity
import com.github.dsbezerra.cashflow.domain.model.RecurringRule
import com.github.dsbezerra.cashflow.domain.model.toDecimal

fun RecurringRuleEntity.toDomain(): RecurringRule = RecurringRule(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    type = type,
    amount = amount.toDecimal(),
    description = description,
    frequency = frequency,
    interval = interval.toInt(),
    startDate = startDate,
    endDate = endDate,
    nextOccurrence = nextOccurrence,
    isActive = isActive,
)

fun RecurringRule.toEntity(): RecurringRuleEntity = RecurringRuleEntity(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    type = type,
    amount = amount.toDouble(),
    description = description,
    frequency = frequency,
    interval = interval.toLong(),
    startDate = startDate,
    endDate = endDate,
    nextOccurrence = nextOccurrence,
    isActive = isActive,
)
