package com.github.dsbezerra.cashflow.core.domain.usecase.transaction

import com.github.dsbezerra.cashflow.core.domain.model.Transaction
import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository

class CreateTransactionUseCase(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(transaction: Transaction) {
        transactionRepository.insert(transaction)
    }
}
