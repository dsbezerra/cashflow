package com.github.dsbezerra.cashflow.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

data class ReportData(
    val period: ReportPeriod,
    val totalIncome: Double,
    val totalExpenses: Double,
    val netBalance: Double,
    val averageDailyExpense: Double,
    val highestExpenseCategory: Category?,
    val mostUsedCategory: Category?,
    val expenseByCategory: List<CategoryAmount>,
    val incomeByCategory: List<CategoryAmount>,
    val monthlyBreakdown: List<MonthlyAmount>,
    val dailyCumulative: List<DailyAmount>,
)

data class CategoryAmount(
    val category: Category,
    val amount: Double,
    val count: Int,
)

data class MonthlyAmount(
    val year: Int,
    val month: Month,
    val income: Double,
    val expenses: Double,
)

data class DailyAmount(
    val date: LocalDate,
    val cumulativeNet: Double,
)
