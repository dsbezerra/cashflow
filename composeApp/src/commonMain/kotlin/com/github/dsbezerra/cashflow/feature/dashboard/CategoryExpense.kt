package com.github.dsbezerra.cashflow.feature.dashboard

data class CategoryExpense(
    val categoryId: String,
    val label: String,
    val colorHex: String,
    val amount: Double,
)
