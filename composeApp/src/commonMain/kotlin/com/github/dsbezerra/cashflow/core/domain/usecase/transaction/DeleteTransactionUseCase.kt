package com.github.dsbezerra.cashflow.core.domain.usecase.transaction

import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first

class DeleteTransactionUseCase(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(id: String) {
        val target = transactionRepository.getById(id) ?: return
        transactionRepository.delete(id)

        // If this transaction is one side of a transfer pair, delete the other side too
        val linkId = target.recurringId ?: return
        val all = transactionRepository.getAll().first()
        val partner = all.firstOrNull { it.recurringId == linkId && it.id != id }
        if (partner != null) {
            transactionRepository.delete(partner.id)
        }
    }
}
