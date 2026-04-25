package com.github.dsbezerra.cashflow.core.domain.repository

import com.github.dsbezerra.cashflow.core.domain.model.RecurringRule
import kotlinx.coroutines.flow.Flow

interface RecurringRuleRepository {
    fun getAll(): Flow<List<RecurringRule>>
    suspend fun getById(id: String): RecurringRule?
    suspend fun insert(rule: RecurringRule)
    suspend fun update(rule: RecurringRule)
    suspend fun delete(id: String)
}
