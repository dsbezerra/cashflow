package com.github.dsbezerra.cashflow.core.domain.usecase.dashboard

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.github.dsbezerra.cashflow.core.domain.model.Account
import com.github.dsbezerra.cashflow.core.domain.model.DashboardSummary
import com.github.dsbezerra.cashflow.core.domain.model.Decimal
import com.github.dsbezerra.cashflow.core.domain.model.MonthlyAmount
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
import com.github.dsbezerra.cashflow.core.domain.model.toDecimal
import com.github.dsbezerra.cashflow.core.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository
import com.github.dsbezerra.cashflow.feature.dashboard.toRecentTransaction
import com.github.dsbezerra.cashflow.util.namePtBr
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class GetDashboardSummaryUseCase(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(accountId: String? = null, year: Int, monthNumber: Int): Flow<DashboardSummary> =
        combine(
            accountRepository.getAll(),
            transactionRepository.getAll(),
        ) { accounts, transactions ->
            val filteredAccounts = if (accountId != null) accounts.filter { it.id == accountId } else accounts
            val filteredTransactions = if (accountId != null) transactions.filter { it.accountId == accountId } else transactions

            val tz = TimeZone.currentSystemDefault()
            val selectedMonth = Month(monthNumber)

            val accountBalances: List<Pair<Account, Decimal>> = filteredAccounts.map { account ->
                val txs = filteredTransactions.filter { it.accountId == account.id }
                val net = txs.sumOf { tx ->
                    when (tx.type) {
                        TransactionType.INCOME -> tx.amount.toDouble()
                        TransactionType.EXPENSE -> -tx.amount.toDouble()
                        TransactionType.TRANSFER -> 0.0
                    }
                }
                account to (account.initialBalance.toDouble() + net).toDecimal()
            }

            val totalBalance = accountBalances.sumOf { it.second.toDouble() }

            val monthlyTxs = filteredTransactions.filter { tx ->
                val local = Instant.fromEpochMilliseconds(tx.date).toLocalDateTime(tz)
                local.month == selectedMonth && local.year == year
            }

            val monthlyIncome = monthlyTxs
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount.toDouble() }
            val monthlyExpenses = monthlyTxs
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount.toDouble() }

            val recentTransactions = monthlyTxs
                .sortedByDescending { it.date }
                .take(10)

            // Last 6 months breakdown anchored to the selected month (oldest first)
            val anchor = LocalDate(year, selectedMonth, 1)
            val last6MonthsBreakdown = (5 downTo 0).map { offset ->
                val targetDate = anchor.minus(DatePeriod(months = offset))
                val targetYear = targetDate.year
                val targetMonth = targetDate.month
                val monthTxs = filteredTransactions.filter { tx ->
                    val local = Instant.fromEpochMilliseconds(tx.date).toLocalDateTime(tz)
                    local.year == targetYear && local.month == targetMonth
                }
                MonthlyAmount(
                    year = targetYear,
                    month = targetMonth,
                    income = monthTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount.toDouble() },
                    expenses = monthTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount.toDouble() },
                )
            }

            DashboardSummary(
                accountBalances = accountBalances,
                monthName = selectedMonth.namePtBr().capitalize(Locale.current),
                year = year,
                transactionCount = monthlyTxs.size,
                totalBalance = totalBalance.toDecimal(),
                monthlyIncome = monthlyIncome.toDecimal(),
                monthlyExpenses = monthlyExpenses.toDecimal(),
                netBalance = (monthlyIncome - monthlyExpenses).toDecimal(),
                recentTransactions = recentTransactions.map { it.toRecentTransaction() },
                last6MonthsBreakdown = last6MonthsBreakdown,
            )
        }
}
