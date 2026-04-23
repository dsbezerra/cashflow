package com.github.dsbezerra.cashflow.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.dsbezerra.cashflow.data.mapper.toDomain
import com.github.dsbezerra.cashflow.data.mapper.toEntity
import com.github.dsbezerra.cashflow.db.BudgetQueries
import com.github.dsbezerra.cashflow.domain.model.Budget
import com.github.dsbezerra.cashflow.domain.repository.BudgetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightBudgetRepository(
    private val queries: BudgetQueries,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : BudgetRepository {

    override fun getAll(): Flow<List<Budget>> =
        queries.selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Budget? = withContext(dispatcher) {
        queries.selectById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun insert(budget: Budget) {
        withContext(dispatcher) { queries.insert(budget.toEntity()) }
    }

    override suspend fun update(budget: Budget) {
        withContext(dispatcher) {
            queries.update(
                categoryId = budget.categoryId,
                amount = budget.amount,
                period = budget.period,
                startDate = budget.startDate,
                isActive = budget.isActive,
                id = budget.id,
            )
        }
    }

    override suspend fun delete(id: String) {
        withContext(dispatcher) { queries.delete(id) }
    }
}
