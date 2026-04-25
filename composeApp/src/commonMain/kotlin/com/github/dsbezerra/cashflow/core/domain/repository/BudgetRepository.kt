package com.github.dsbezerra.cashflow.core.domain.repository

import com.github.dsbezerra.cashflow.core.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getAll(): Flow<List<Budget>>
    suspend fun getById(id: String): Budget?
    suspend fun insert(budget: Budget)
    suspend fun update(budget: Budget)
    suspend fun delete(id: String)
}
