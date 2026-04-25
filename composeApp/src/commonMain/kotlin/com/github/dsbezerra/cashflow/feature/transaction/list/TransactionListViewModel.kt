package com.github.dsbezerra.cashflow.feature.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository
import com.github.dsbezerra.cashflow.core.domain.usecase.transaction.DeleteTransactionUseCase
import com.github.dsbezerra.cashflow.util.safeRunCatching
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

class TransactionListViewModel(
    private val transactionRepository: TransactionRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
) : ViewModel() {

    private val _events = Channel<TransactionListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val transactions: Flow<PagingData<TransactionListItem>> =
        transactionRepository.getPagedTransactions(PAGE_SIZE)
            .map { pagingData ->
                pagingData
                    .map { tx -> TransactionListItem.Entry(tx) as TransactionListItem }
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
            .cachedIn(viewModelScope)

    fun onAction(action: TransactionListAction) {
        when (action) {
            is TransactionListAction.DeleteTransaction -> viewModelScope.launch {
                safeRunCatching { deleteTransactionUseCase(action.id) }
                    .onFailure { _events.send(TransactionListEvent.ShowError("Failed to delete transaction")) }
            }
        }
    }

    private fun Long.toLocalDate() =
        Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault()).date

    companion object {
        private const val PAGE_SIZE = 30
    }
}
