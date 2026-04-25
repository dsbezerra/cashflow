package com.github.dsbezerra.cashflow.core.domain.model

data class Budget(
    val id: String,
    val categoryId: String,
    val amount: Decimal,
    val period: BudgetPeriod,
    val startDate: Long,
    val isActive: Boolean,
)
