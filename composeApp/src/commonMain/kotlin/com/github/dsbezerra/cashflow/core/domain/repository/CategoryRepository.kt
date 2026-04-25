package com.github.dsbezerra.cashflow.core.domain.repository

import com.github.dsbezerra.cashflow.core.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAll(): Flow<List<Category>>
    suspend fun getById(id: String): Category?
    suspend fun insert(category: Category)
    suspend fun update(category: Category)
    suspend fun delete(id: String)
}
