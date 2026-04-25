package com.github.dsbezerra.cashflow.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.dsbezerra.cashflow.core.data.mapper.toDomain
import com.github.dsbezerra.cashflow.core.data.mapper.toEntity
import com.github.dsbezerra.cashflow.db.TransactionQueries
import com.github.dsbezerra.cashflow.core.domain.model.Transaction
import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightTransactionRepository(
    private val queries: TransactionQueries,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : TransactionRepository {

    override fun getAll(): Flow<List<Transaction>> =
        queries.selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .map { list -> list.map { it.toDomain() } }

    override fun getByAccount(accountId: String): Flow<List<Transaction>> =
        queries.selectByAccount(accountId)
            .asFlow()
            .mapToList(dispatcher)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Transaction? = withContext(dispatcher) {
        queries.selectById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun insert(transaction: Transaction) {
        withContext(dispatcher) { queries.insert(transaction.toEntity()) }
    }

    override suspend fun update(transaction: Transaction) {
        withContext(dispatcher) {
            queries.update(
                accountId = transaction.accountId,
                categoryId = transaction.categoryId,
                type = transaction.type,
                amount = transaction.amount.toDouble(),
                description = transaction.description,
                date = transaction.date,
                notes = transaction.notes,
                attachmentPath = transaction.attachmentPath,
                isRecurring = transaction.isRecurring,
                recurringId = transaction.recurringId,
                updatedAt = transaction.updatedAt,
                id = transaction.id,
            )
        }
    }

    override suspend fun delete(id: String) {
        withContext(dispatcher) { queries.delete(id) }
    }
}
