package com.github.dsbezerra.cashflow.core.domain.model

data class DreReport(
    val year: Int,
    val month: Int,
    val grossRevenue: DreLineItem,
    val deductions: DreLineItem,
    val netRevenue: Double,
    val costs: DreLineItem,
    val grossProfit: Double,
    val operationalExpenses: DreLineItem,
    val operationalResult: Double,
    val financialExpenses: DreLineItem,
    val netResult: Double,
)

data class DreLineItem(
    val total: Double,
    val categories: List<DreCategoryLine>,
)

data class DreCategoryLine(
    val category: Category,
    val amount: Double,
)
