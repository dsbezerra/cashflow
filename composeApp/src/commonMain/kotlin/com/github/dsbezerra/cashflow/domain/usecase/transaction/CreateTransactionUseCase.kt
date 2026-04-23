package com.github.dsbezerra.cashflow.domain.usecase.transaction

import com.github.dsbezerra.cashflow.domain.model.Transaction
import com.github.dsbezerra.cashflow.domain.repository.TransactionRepository

class CreateTransactionUseCase(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(transaction: Transaction) {
        transactionRepository.insert(transaction)
    }
}
