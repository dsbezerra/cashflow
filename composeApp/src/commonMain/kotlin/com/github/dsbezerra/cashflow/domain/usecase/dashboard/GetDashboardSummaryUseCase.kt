package com.github.dsbezerra.cashflow.domain.usecase.dashboard

import com.github.dsbezerra.cashflow.domain.model.Account
import com.github.dsbezerra.cashflow.domain.model.DashboardSummary
import com.github.dsbezerra.cashflow.domain.model.MonthlyAmount
import com.github.dsbezerra.cashflow.domain.model.TransactionType
import com.github.dsbezerra.cashflow.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class GetDashboardSummaryUseCase(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(): Flow<DashboardSummary> =
        combine(
            accountRepository.getAll(),
            transactionRepository.getAll(),
        ) { accounts, transactions ->
            val tz = TimeZone.currentSystemDefault()
            val now = Clock.System.now()
            val localNow = now.toLocalDateTime(tz)
            val currentMonth = localNow.month
            val currentYear = localNow.year

            val accountBalances: List<Pair<Account, Double>> = accounts.map { account ->
                val txs = transactions.filter { it.accountId == account.id }
                val net = txs.sumOf { tx ->
                    when (tx.type) {
                        TransactionType.INCOME -> tx.amount
                        TransactionType.EXPENSE -> -tx.amount
                        TransactionType.TRANSFER -> 0.0
                    }
                }
                account to (account.initialBalance + net)
            }

            val totalBalance = accountBalances.sumOf { it.second }

            val today = localNow.date

            val monthlyTxs = transactions.filter { tx ->
                val local = Instant.fromEpochMilliseconds(tx.date).toLocalDateTime(tz)
                local.month == currentMonth && local.year == currentYear
            }

            val monthlyIncome = monthlyTxs
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
            val monthlyExpenses = monthlyTxs
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            val recentTransactions = transactions
                .sortedByDescending { it.date }
                .take(10)

            // Last 6 months breakdown (oldest first)
            val last6MonthsBreakdown = (5 downTo 0).map { offset ->
                val targetDate = today.minus(DatePeriod(months = offset))
                val targetYear = targetDate.year
                val targetMonth = targetDate.month
                val monthTxs = transactions.filter { tx ->
                    val local = Instant.fromEpochMilliseconds(tx.date).toLocalDateTime(tz)
                    local.year == targetYear && local.month == targetMonth
                }
                MonthlyAmount(
                    year = targetYear,
                    month = targetMonth,
                    income = monthTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
                    expenses = monthTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount },
                )
            }

            DashboardSummary(
                accountBalances = accountBalances,
                totalBalance = totalBalance,
                monthlyIncome = monthlyIncome,
                monthlyExpenses = monthlyExpenses,
                netBalance = monthlyIncome - monthlyExpenses,
                recentTransactions = recentTransactions,
                last6MonthsBreakdown = last6MonthsBreakdown,
            )
        }
}
