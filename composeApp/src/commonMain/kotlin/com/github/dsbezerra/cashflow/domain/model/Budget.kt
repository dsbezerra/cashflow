package com.github.dsbezerra.cashflow.domain.model

data class Budget(
    val id: String,
    val categoryId: String,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: Long,
    val isActive: Boolean,
)
