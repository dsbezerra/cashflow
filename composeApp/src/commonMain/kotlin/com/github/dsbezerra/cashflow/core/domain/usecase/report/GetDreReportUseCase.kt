package com.github.dsbezerra.cashflow.core.domain.usecase.report

import com.github.dsbezerra.cashflow.core.domain.model.DreCategoryLine
import com.github.dsbezerra.cashflow.core.domain.model.DreClassification
import com.github.dsbezerra.cashflow.core.domain.model.DreLineItem
import com.github.dsbezerra.cashflow.core.domain.model.DreReport
import com.github.dsbezerra.cashflow.core.domain.repository.CategoryRepository
import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GetDreReportUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(year: Int, month: Int, accountId: String? = null): Flow<DreReport> =
        combine(transactionRepository.getAll(), categoryRepository.getAll()) { transactions, categories ->
            val tz = TimeZone.currentSystemDefault()
            val catMap = categories.associateBy { it.id }

            val accountFiltered = if (accountId != null) transactions.filter { it.accountId == accountId } else transactions
            val filtered = accountFiltered.filter { tx ->
                val d = Instant.fromEpochMilliseconds(tx.date).toLocalDateTime(tz).date
                d.year == year && d.monthNumber == month
            }

            fun lineItem(classification: DreClassification): DreLineItem {
                val lines = filtered
                    .groupBy { it.categoryId }
                    .mapNotNull { (catId, txs) ->
                        val cat = catMap[catId] ?: return@mapNotNull null
                        if (cat.dreClassification != classification) return@mapNotNull null
                        DreCategoryLine(cat, txs.sumOf { it.amount.toDouble() })
                    }
                    .sortedByDescending { it.amount }
                return DreLineItem(total = lines.sumOf { it.amount }, categories = lines)
            }

            val grossRevenue = lineItem(DreClassification.GROSS_REVENUE)
            val deductions = lineItem(DreClassification.DEDUCTION)
            val netRevenue = grossRevenue.total - deductions.total
            val costs = lineItem(DreClassification.COST)
            val grossProfit = netRevenue - costs.total
            val operationalExpenses = lineItem(DreClassification.EXPENSE)
            val operationalResult = grossProfit - operationalExpenses.total
            val financialExpenses = lineItem(DreClassification.FINANCIAL_EXPENSE)
            val netResult = operationalResult - financialExpenses.total

            DreReport(
                year = year,
                month = month,
                grossRevenue = grossRevenue,
                deductions = deductions,
                netRevenue = netRevenue,
                costs = costs,
                grossProfit = grossProfit,
                operationalExpenses = operationalExpenses,
                operationalResult = operationalResult,
                financialExpenses = financialExpenses,
                netResult = netResult,
            )
        }
}
