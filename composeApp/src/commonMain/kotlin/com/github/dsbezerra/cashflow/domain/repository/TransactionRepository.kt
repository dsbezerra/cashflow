package com.github.dsbezerra.cashflow.domain.repository

import com.github.dsbezerra.cashflow.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAll(): Flow<List<Transaction>>
    fun getByAccount(accountId: String): Flow<List<Transaction>>
    suspend fun getById(id: String): Transaction?
    suspend fun insert(transaction: Transaction)
    suspend fun update(transaction: Transaction)
    suspend fun delete(id: String)
}
