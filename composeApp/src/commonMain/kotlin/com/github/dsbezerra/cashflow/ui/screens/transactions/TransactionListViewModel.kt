package com.github.dsbezerra.cashflow.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.domain.repository.TransactionRepository
import com.github.dsbezerra.cashflow.domain.usecase.transaction.DeleteTransactionUseCase
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

    private val _state = MutableStateFlow(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionListState())
    val state = _state.asStateFlow()

    private val _events = Channel<com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var collectJob: Job? = null

    init {
        startCollecting()
    }

    fun onAction(action: com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionListAction) {
        when (action) {
            is com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionListAction.Refresh -> {
                collectJob?.cancel()
                startCollecting()
            }
            is com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionListAction.DeleteTransaction -> {
                viewModelScope.launch {
                    runCatching { deleteTransactionUseCase(action.id) }
                        .onFailure {
                            _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionListEvent.ShowError("Failed to delete transaction"))
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
