package com.github.dsbezerra.cashflow.domain.model

data class BudgetAlert(
    val categoryId: String,
    val categoryName: String,
    val budgetAmount: Double,
    val currentSpend: Double,
    val status: BudgetStatus,
)
