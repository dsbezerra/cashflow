package com.github.dsbezerra.cashflow.core.domain.usecase.transaction

import com.github.dsbezerra.cashflow.core.domain.model.Transaction
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
import com.github.dsbezerra.cashflow.core.domain.model.toDecimal
import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository
import com.github.dsbezerra.cashflow.util.generateId
import kotlin.time.Clock

class CreateTransferUseCase(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(
        fromAccountId: String,
        toAccountId: String,
        amount: Double,
        description: String,
        date: Long,
        categoryId: String,
        notes: String? = null,
    ) {
        // Shared link ID marks the expense/income pair as a single transfer
        val linkId = generateId()
        val now = Clock.System.now().toEpochMilliseconds()

        val expense = Transaction(
            id = generateId(),
            accountId = fromAccountId,
            categoryId = categoryId,
            type = TransactionType.EXPENSE,
            amount = amount.toDecimal(),
            description = description,
            date = date,
            notes = notes,
            attachmentPath = null,
            isRecurring = false,
            recurringId = linkId,
            createdAt = now,
            updatedAt = now,
        )
        val income = Transaction(
            id = generateId(),
            accountId = toAccountId,
            categoryId = categoryId,
            type = TransactionType.INCOME,
            amount = amount.toDecimal(),
            description = description,
            date = date,
            notes = notes,
            attachmentPath = null,
            isRecurring = false,
            recurringId = linkId,
            createdAt = now,
            updatedAt = now,
        )

        transactionRepository.insert(expense)
        transactionRepository.insert(income)
    }
}
