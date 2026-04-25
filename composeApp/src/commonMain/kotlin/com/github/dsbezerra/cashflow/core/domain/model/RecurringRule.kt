package com.github.dsbezerra.cashflow.core.domain.model

data class RecurringRule(
    val id: String,
    val accountId: String,
    val categoryId: String,
    val type: TransactionType,
    val amount: Decimal,
    val description: String,
    val frequency: Frequency,
    val interval: Int,
    val startDate: Long,
    val endDate: Long?,
    val nextOccurrence: Long,
    val isActive: Boolean,
)
