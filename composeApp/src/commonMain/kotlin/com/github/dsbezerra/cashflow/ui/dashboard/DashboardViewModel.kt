package com.github.dsbezerra.cashflow.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.domain.repository.CategoryRepository
import com.github.dsbezerra.cashflow.domain.usecase.dashboard.GetDashboardSummaryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getDashboardSummary: GetDashboardSummaryUseCase,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _events = Channel<DashboardEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var collectJob: Job? = null

    init {
        startCollecting()
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            DashboardAction.Refresh -> {
                collectJob?.cancel()
                startCollecting()
            }
        }
    }

    private fun startCollecting() {
        collectJob = viewModelScope.launch {
            runCatching {
                combine(
                    getDashboardSummary(),
                    categoryRepository.getAll(),
                ) { summary, cats ->
                    DashboardState(
                        isLoading = false,
                        summary = summary,
                        categories = cats.associateBy { it.id },
                    )
                }.collect { _state.value = it }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
                _events.send(DashboardEvent.ShowError("Erro ao carregar painel"))
            }
        }
    }
}
