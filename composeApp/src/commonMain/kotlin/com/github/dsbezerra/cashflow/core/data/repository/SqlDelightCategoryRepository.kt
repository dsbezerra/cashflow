package com.github.dsbezerra.cashflow.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.dsbezerra.cashflow.core.data.mapper.toDomain
import com.github.dsbezerra.cashflow.core.data.mapper.toEntity
import com.github.dsbezerra.cashflow.db.CategoryQueries
import com.github.dsbezerra.cashflow.core.domain.model.Category
import com.github.dsbezerra.cashflow.core.domain.repository.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightCategoryRepository(
    private val queries: CategoryQueries,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : CategoryRepository {

    override fun getAll(): Flow<List<Category>> =
        queries.selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Category? = withContext(dispatcher) {
        queries.selectById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun insert(category: Category) {
        withContext(dispatcher) { queries.insert(category.toEntity()) }
    }

    override suspend fun update(category: Category) {
        withContext(dispatcher) {
            queries.update(
                name = category.name,
                type = category.type,
                icon = category.icon,
                color = category.color,
                parentId = category.parentId,
                isDefault = category.isDefault,
                isArchived = category.isArchived,
                dreClassification = category.dreClassification,
                id = category.id,
            )
        }
    }

    override suspend fun delete(id: String) {
        withContext(dispatcher) { queries.delete(id) }
    }
}
