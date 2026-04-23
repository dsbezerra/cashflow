package com.github.dsbezerra.cashflow.domain.usecase.account

import com.github.dsbezerra.cashflow.domain.model.Account
import com.github.dsbezerra.cashflow.domain.model.TransactionType
import com.github.dsbezerra.cashflow.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first

class GetAccountBalanceUseCase(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(account: Account): Double {
        val transactions = transactionRepository.getByAccount(account.id).first()
        val net = transactions.sumOf { tx ->
            when (tx.type) {
                TransactionType.INCOME -> tx.amount
                TransactionType.EXPENSE -> -tx.amount
                TransactionType.TRANSFER -> 0.0 // handled by the paired EXPENSE/INCOME entries
            }
        }
        return account.initialBalance + net
    }
}
