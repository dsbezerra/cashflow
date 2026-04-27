package com.github.dsbezerra.cashflow.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.dsbezerra.cashflow.core.data.mapper.toDomain
import com.github.dsbezerra.cashflow.core.data.mapper.toEntity
import com.github.dsbezerra.cashflow.db.AccountQueries
import com.github.dsbezerra.cashflow.core.domain.model.Account
import com.github.dsbezerra.cashflow.core.domain.repository.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightAccountRepository(
    private val queries: AccountQueries,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : AccountRepository {

    override fun getAll(): Flow<List<Account>> =
        queries.selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Account? = withContext(dispatcher) {
        queries.selectById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun getDefault(): Account? = withContext(dispatcher) {
        queries.selectDefault().executeAsOneOrNull()?.toDomain()
    }

    override suspend fun insert(account: Account) {
        withContext(dispatcher) { queries.insert(account.toEntity()) }
    }

    override suspend fun update(account: Account) {
        withContext(dispatcher) {
            queries.update(
                name = account.name,
                type = account.type,
                currency = account.currency,
                initialBalance = account.initialBalance.toDouble(),
                icon = account.icon,
                isArchived = account.isArchived,
                id = account.id,
            )
        }
    }

    override suspend fun setDefault(id: String) {
        withContext(dispatcher) { queries.setDefault(id) }
    }

    override suspend fun delete(id: String) {
        withContext(dispatcher) { queries.delete(id) }
    }
}
