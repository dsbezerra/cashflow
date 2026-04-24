package com.github.dsbezerra.cashflow.domain.repository

import com.github.dsbezerra.cashflow.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAll(): Flow<List<Account>>
    suspend fun getById(id: String): Account?
    suspend fun getDefault(): Account?
    suspend fun insert(account: Account)
    suspend fun update(account: Account)
    suspend fun setDefault(id: String)
    suspend fun delete(id: String)
}
