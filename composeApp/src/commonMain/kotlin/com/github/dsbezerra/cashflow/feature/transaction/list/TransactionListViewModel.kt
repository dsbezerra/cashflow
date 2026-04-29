package com.github.dsbezerra.cashflow.feature.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import co.touchlab.kermit.Logger
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
import com.github.dsbezerra.cashflow.core.domain.repository.CategoryRepository
import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository
import com.github.dsbezerra.cashflow.core.domain.usecase.transaction.DeleteTransactionUseCase
import com.github.dsbezerra.cashflow.util.safeRunCatching
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

class TransactionListViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val logger: Logger,
) : ViewModel() {

    private val _events = Channel<TransactionListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _typeFilter = MutableStateFlow<TransactionType?>(null)
    val typeFilter: StateFlow<TransactionType?> = _typeFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: Flow<PagingData<TransactionListItem>> =
        combine(
            categoryRepository.getAll().map { list -> list.associate { it.id to it.name } },
            _typeFilter,
            _searchQuery,
        ) { categoryMap, type, query -> Triple(categoryMap, type, query) }
            .flatMapLatest { (categoryMap, type, query) ->
                val effectiveQuery = query.trim().takeIf { it.isNotEmpty() }
                transactionRepository.getPagedTransactions(PAGE_SIZE, type, effectiveQuery)
                    .map { pagingData ->
                        pagingData
                            .map { tx -> TransactionListItem.Entry(tx.toUiModel(categoryMap)) as TransactionListItem }
                            .insertSeparators { before, after ->
                                val beforeDate = (before as? TransactionListItem.Entry)
                                    ?.transaction?.date?.toLocalDate()
                                val afterDate = (after as? TransactionListItem.Entry)
                                    ?.transaction?.date?.toLocalDate()
                                if (afterDate != null && afterDate != beforeDate)
                                    TransactionListItem.Header(afterDate)
                                else null
                            }
                    }
            }
            .cachedIn(viewModelScope)

    fun onAction(action: TransactionListAction) {
        when (action) {
            is TransactionListAction.DeleteTransaction -> viewModelScope.launch {
                logger.d { "Deleting transaction: id=${action.id}" }
                safeRunCatching { deleteTransactionUseCase(action.id) }
                    .onFailure { e ->
                        logger.e(e) { "Failed to delete transaction: id=${action.id}" }
                        _events.send(TransactionListEvent.ShowError("Failed to delete transaction"))
                    }
            }
            is TransactionListAction.TypeFilterChanged -> _typeFilter.value = action.type
            is TransactionListAction.SearchQueryChanged -> _searchQuery.value = action.query
        }
    }

    private fun Long.toLocalDate() =
        Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault()).date

    companion object {
        private const val PAGE_SIZE = 30
    }
}
