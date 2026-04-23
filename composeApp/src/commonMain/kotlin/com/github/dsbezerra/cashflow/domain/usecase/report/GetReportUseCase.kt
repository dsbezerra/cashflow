package com.github.dsbezerra.cashflow.domain.usecase.report

import com.github.dsbezerra.cashflow.domain.model.CategoryAmount
import com.github.dsbezerra.cashflow.domain.model.DailyAmount
import com.github.dsbezerra.cashflow.domain.model.MonthlyAmount
import com.github.dsbezerra.cashflow.domain.model.ReportData
import com.github.dsbezerra.cashflow.domain.model.ReportPeriod
import com.github.dsbezerra.cashflow.domain.model.Transaction
import com.github.dsbezerra.cashflow.domain.model.TransactionType
import com.github.dsbezerra.cashflow.domain.repository.CategoryRepository
import com.github.dsbezerra.cashflow.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class GetReportUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(period: ReportPeriod): Flow<ReportData> =
        combine(transactionRepository.getAll(), categoryRepository.getAll()) { transactions, categories ->
            val tz = TimeZone.currentSystemDefault()
            val today = Clock.System.now().toLocalDateTime(tz).date
            val (start, end) = period.toDateRange(today)
            val catMap = categories.associateBy { it.id }

            val filtered = transactions.filter { tx ->
                val d = Instant.fromEpochMilliseconds(tx.date).toLocalDateTime(tz).date
                d >= start && d <= end
            }

            val income = filtered.filter { it.type == TransactionType.INCOME }
            val expenses = filtered.filter { it.type == TransactionType.EXPENSE }

            val totalIncome = income.sumOf { it.amount }
            val totalExpenses = expenses.sumOf { it.amount }
            val netBalance = totalIncome - totalExpenses

            val days = (end.toEpochDays() - start.toEpochDays() + 1).coerceAtLeast(1)
            val averageDailyExpense = totalExpenses / days

            val expenseByCategory = expenses
                .groupBy { it.categoryId }
                .mapNotNull { (catId, txs) ->
                    val cat = catMap[catId] ?: return@mapNotNull null
                    CategoryAmount(cat, txs.sumOf { it.amount }, txs.size)
                }
                .sortedByDescending { it.amount }

            val incomeByCategory = income
                .groupBy { it.categoryId }
                .mapNotNull { (catId, txs) ->
                    val cat = catMap[catId] ?: return@mapNotNull null
                    CategoryAmount(cat, txs.sumOf { it.amount }, txs.size)
                }
                .sortedByDescending { it.amount }

            val highestExpenseCategory = expenseByCategory.firstOrNull()?.category
            val mostUsedCategory = filtered
                .groupBy { it.categoryId }
                .maxByOrNull { it.value.size }
                ?.key?.let { catMap[it] }

            // Monthly breakdown
            val monthlyBreakdown = filtered
                .groupBy { tx ->
                    val d = Instant.fromEpochMilliseconds(tx.date).toLocalDateTime(tz).date
                    Pair(d.year, d.month)
                }
                .map { (yearMonth, txs) ->
                    MonthlyAmount(
                        year = yearMonth.first,
                        month = yearMonth.second,
                        income = txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
                        expenses = txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount },
                    )
                }
                .sortedWith(compareBy({ it.year }, { it.month.ordinal }))

            // Daily cumulative net
            val dailyCumulative = buildDailyCumulative(start, end, filtered, tz)

            ReportData(
                period = period,
                totalIncome = totalIncome,
                totalExpenses = totalExpenses,
                netBalance = netBalance,
                averageDailyExpense = averageDailyExpense,
                highestExpenseCategory = highestExpenseCategory,
                mostUsedCategory = mostUsedCategory,
                expenseByCategory = expenseByCategory,
                incomeByCategory = incomeByCategory,
                monthlyBreakdown = monthlyBreakdown,
                dailyCumulative = dailyCumulative,
            )
        }

    private fun buildDailyCumulative(
        start: LocalDate,
        end: LocalDate,
        transactions: List<Transaction>,
        tz: TimeZone,
    ): List<DailyAmount> {
        val byDate = transactions.groupBy { tx ->
            Instant.fromEpochMilliseconds(tx.date).toLocalDateTime(tz).date
        }

        val result = mutableListOf<DailyAmount>()
        var cumulative = 0.0
        var current = start
        while (current <= end) {
            val dayTxs = byDate[current] ?: emptyList()
            val dayNet = dayTxs.sumOf { tx ->
                when (tx.type) {
                    TransactionType.INCOME -> tx.amount
                    TransactionType.EXPENSE -> -tx.amount
                    else -> 0.0
                }
            }
            cumulative += dayNet
            result.add(DailyAmount(current, cumulative))
            current = current.plus(DatePeriod(days = 1))
        }
        return result
    }
}

private fun ReportPeriod.toDateRange(today: LocalDate): Pair<LocalDate, LocalDate> = when (this) {
    ReportPeriod.THIS_MONTH -> {
        val start = LocalDate(today.year, today.month, 1)
        Pair(start, today)
    }
    ReportPeriod.LAST_MONTH -> {
        val firstOfThisMonth = LocalDate(today.year, today.month, 1)
        val lastOfPrev = firstOfThisMonth.minus(DatePeriod(days = 1))
        val firstOfPrev = LocalDate(lastOfPrev.year, lastOfPrev.month, 1)
        Pair(firstOfPrev, lastOfPrev)
    }
    ReportPeriod.LAST_3_MONTHS -> {
        val start = today.minus(DatePeriod(months = 3))
        Pair(start, today)
    }
    ReportPeriod.LAST_6_MONTHS -> {
        val start = today.minus(DatePeriod(months = 6))
        Pair(start, today)
    }
    ReportPeriod.THIS_YEAR -> {
        val start = LocalDate(today.year, Month.JANUARY, 1)
        Pair(start, today)
    }
}
