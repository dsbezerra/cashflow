package com.github.dsbezerra.cashflow.domain.model

data class RecurringRule(
    val id: String,
    val accountId: String,
    val categoryId: String,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val frequency: Frequency,
    val interval: Int,
    val startDate: Long,
    val endDate: Long?,
    val nextOccurrence: Long,
    val isActive: Boolean,
)
