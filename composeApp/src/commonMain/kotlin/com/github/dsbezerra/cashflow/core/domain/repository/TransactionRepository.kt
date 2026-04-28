package com.github.dsbezerra.cashflow.core.domain.repository

import androidx.paging.PagingData
import com.github.dsbezerra.cashflow.core.domain.model.Transaction
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAll(): Flow<List<Transaction>>
    fun getByAccount(accountId: String): Flow<List<Transaction>>
    fun getPagedTransactions(
        pageSize: Int,
        type: TransactionType? = null,
        query: String? = null,
    ): Flow<PagingData<Transaction>>
    suspend fun getById(id: String): Transaction?
    suspend fun insert(transaction: Transaction)
    suspend fun update(transaction: Transaction)
    suspend fun delete(id: String)
}
