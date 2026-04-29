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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

class TransactionListViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val logger: Logger,
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionListState())
    val state: StateFlow<TransactionListState> = _state.asStateFlow()

    private val _events = Channel<TransactionListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _typeFilter = MutableStateFlow<TransactionType?>(null)
    val typeFilter: StateFlow<TransactionType?> = _typeFilter.asStateFlow()

    private val _categoryFilter = MutableStateFlow<String?>(null)
    val categoryFilter: StateFlow<String?> = _categoryFilter.asStateFlow()

    private val _periodFilter = MutableStateFlow<TransactionListPeriod?>(null)
    val periodFilter: StateFlow<TransactionListPeriod?> = _periodFilter.asStateFlow()

    val activeFilterCount: StateFlow<Int> = combine(_typeFilter, _categoryFilter, _periodFilter)
    { type, cat, period -> listOfNotNull(type, cat, period).size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    init {
        viewModelScope.launch {
            categoryRepository.getAll().collect { categories ->
                _state.update { it.copy(categories = categories) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: Flow<PagingData<TransactionListItem>> =
        combine(
            categoryRepository.getAll().map { list -> list.associate { it.id to it.name } },
            _typeFilter,
            _searchQuery,
            _categoryFilter,
            _periodFilter,
        ) { categoryMap, type, query, catId, period ->
            Params(
                categoryMap = categoryMap,
                type = type,
                query = query,
                categoryId = catId,
                period = period,
            )
        }
            .flatMapLatest { params ->
                val effectiveQuery = params.query.trim().takeIf { it.isNotEmpty() }
                val (startDate, endDate) = params.period.toDateRange()
                transactionRepository.getPagedTransactions(
                    pageSize = PAGE_SIZE,
                    type = params.type,
                    query = effectiveQuery,
                    categoryId = params.categoryId,
                    startDate = startDate,
                    endDate = endDate,
                )
                    .map { pagingData ->
                        pagingData
                            .map { tx ->
                                TransactionListItem.Entry(
                                    tx.toUiModel(params.categoryMap)
                                ) as TransactionListItem
                            }
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
            is TransactionListAction.SearchQueryChanged -> _searchQuery.value = action.query
            is TransactionListAction.ApplyFilters -> {
                _typeFilter.value = action.type
                _categoryFilter.value = action.categoryId
                _periodFilter.value = action.period
            }
        }
    }

    private fun Long.toLocalDate() =
        Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun TransactionListPeriod?.toDateRange(): Pair<Long?, Long?> {
        if (this == null) return null to null
        val tz = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(tz).date
        val (start, end) = when (this) {
            TransactionListPeriod.THIS_WEEK -> {
                val weekStart = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
                weekStart to weekStart.plus(7, DateTimeUnit.DAY)
            }
            TransactionListPeriod.THIS_MONTH -> {
                val monthStart = LocalDate(today.year, today.month, 1)
                monthStart to monthStart.plus(1, DateTimeUnit.MONTH)
            }
            TransactionListPeriod.LAST_MONTH -> {
                val lastMonthStart = LocalDate(today.year, today.month, 1)
                    .minus(1, DateTimeUnit.MONTH)
                lastMonthStart to lastMonthStart.plus(1, DateTimeUnit.MONTH)
            }
            TransactionListPeriod.THIS_YEAR -> {
                val yearStart = LocalDate(today.year, 1, 1)
                yearStart to yearStart.plus(1, DateTimeUnit.YEAR)
            }
        }
        return start.atStartOfDayIn(tz).toEpochMilliseconds() to
                end.atStartOfDayIn(tz).toEpochMilliseconds()
    }

    private data class Params(
        val categoryMap: Map<String, String>,
        val type: TransactionType?,
        val query: String,
        val categoryId: String?,
        val period: TransactionListPeriod?,
    )

    companion object {
        private const val PAGE_SIZE = 30
    }
}
