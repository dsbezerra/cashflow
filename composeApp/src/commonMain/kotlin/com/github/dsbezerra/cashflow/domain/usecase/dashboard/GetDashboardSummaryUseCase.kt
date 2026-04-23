package com.github.dsbezerra.cashflow.domain.usecase.dashboard

import com.github.dsbezerra.cashflow.domain.model.Account
import com.github.dsbezerra.cashflow.domain.model.DashboardSummary
import com.github.dsbezerra.cashflow.domain.model.TransactionType
import com.github.dsbezerra.cashflow.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GetDashboardSummaryUseCase(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(): Flow<DashboardSummary> =
        combine(
            accountRepository.getAll(),
            transactionRepository.getAll(),
        ) { accounts, transactions ->
            val now = Clock.System.now()
            val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault())
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

            val monthlyTxs = transactions.filter { tx ->
                val local = kotlinx.datetime.Instant.fromEpochMilliseconds(tx.date)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                local.month == currentMonth && local.year == currentYear
            }

            val monthlyIncome = monthlyTxs
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
            val monthlyExpenses = monthlyTxs
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            val recentTransactions = transactions.take(10)

            DashboardSummary(
                accountBalances = accountBalances,
                totalBalance = totalBalance,
                monthlyIncome = monthlyIncome,
                monthlyExpenses = monthlyExpenses,
                netBalance = monthlyIncome - monthlyExpenses,
                recentTransactions = recentTransactions,
            )
        }
}
