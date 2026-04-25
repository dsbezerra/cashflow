package com.github.dsbezerra.cashflow.feature.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository
import com.github.dsbezerra.cashflow.core.domain.usecase.transaction.DeleteTransactionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionListViewModel(
    private val transactionRepository: TransactionRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionListState())
    val state = _state.asStateFlow()

    private val _events = Channel<TransactionListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var collectJob: Job? = null

    init {
        startCollecting()
    }

    fun onAction(action: TransactionListAction) {
        when (action) {
            is TransactionListAction.Refresh -> {
                collectJob?.cancel()
                startCollecting()
            }
            is TransactionListAction.DeleteTransaction -> {
                viewModelScope.launch {
                    runCatching { deleteTransactionUseCase(action.id) }
                        .onFailure {
                            _events.send(TransactionListEvent.ShowError("Failed to delete transaction"))
                        }
                }
            }
        }
    }

    private fun startCollecting() {
        collectJob = viewModelScope.launch {
            transactionRepository.getAll().collect { transactions ->
                _state.update {
                    it.copy(
                        transactions = transactions.sortedByDescending { tx -> tx.date },
                        isLoading = false,
                    )
                }
            }
        }
    }
}
