package com.github.dsbezerra.cashflow.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.dsbezerra.cashflow.data.mapper.toDomain
import com.github.dsbezerra.cashflow.data.mapper.toEntity
import com.github.dsbezerra.cashflow.db.RecurringRuleQueries
import com.github.dsbezerra.cashflow.domain.model.RecurringRule
import com.github.dsbezerra.cashflow.domain.repository.RecurringRuleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightRecurringRuleRepository(
    private val queries: RecurringRuleQueries,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : RecurringRuleRepository {

    override fun getAll(): Flow<List<RecurringRule>> =
        queries.selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): RecurringRule? = withContext(dispatcher) {
        queries.selectById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun insert(rule: RecurringRule) {
        withContext(dispatcher) { queries.insert(rule.toEntity()) }
    }

    override suspend fun update(rule: RecurringRule) {
        withContext(dispatcher) {
            queries.update(
                accountId = rule.accountId,
                categoryId = rule.categoryId,
                type = rule.type,
                amount = rule.amount,
                description = rule.description,
                frequency = rule.frequency,
                interval = rule.interval.toLong(),
                startDate = rule.startDate,
                endDate = rule.endDate,
                nextOccurrence = rule.nextOccurrence,
                isActive = rule.isActive,
                id = rule.id,
            )
        }
    }

    override suspend fun delete(id: String) {
        withContext(dispatcher) { queries.delete(id) }
    }
}
