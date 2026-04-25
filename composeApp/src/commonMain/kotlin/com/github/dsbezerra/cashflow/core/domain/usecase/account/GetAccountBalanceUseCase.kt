package com.github.dsbezerra.cashflow.core.domain.usecase.account

import com.github.dsbezerra.cashflow.core.domain.model.Account
import com.github.dsbezerra.cashflow.core.domain.model.Decimal
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
import com.github.dsbezerra.cashflow.core.domain.model.toDecimal
import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first

class GetAccountBalanceUseCase(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(account: Account): Decimal {
        val transactions = transactionRepository.getByAccount(account.id).first()
        val net = transactions.sumOf { tx ->
            when (tx.type) {
                TransactionType.INCOME -> tx.amount.toDouble()
                TransactionType.EXPENSE -> -tx.amount.toDouble()
                TransactionType.TRANSFER -> 0.0 // handled by the paired EXPENSE/INCOME entries
            }
        }
        return (account.initialBalance.toDouble() + net).toDecimal()
    }
}
