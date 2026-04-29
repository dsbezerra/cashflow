package com.github.dsbezerra.cashflow.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.github.dsbezerra.cashflow.core.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.core.domain.repository.CategoryRepository
import com.github.dsbezerra.cashflow.core.domain.usecase.dashboard.GetDashboardSummaryUseCase
import com.github.dsbezerra.cashflow.feature.dashboard.mapCategories
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import com.github.dsbezerra.cashflow.util.safeRunCatching
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DashboardViewModel(
    private val getDashboardSummary: GetDashboardSummaryUseCase,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
    private val logger: Logger,
) : ViewModel() {

    private val _state = MutableStateFlow(run {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        DashboardState(selectedYear = now.year, selectedMonth = now.monthNumber)
    })
    val state = _state.asStateFlow()

    private val _events = Channel<DashboardEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var collectJob: Job? = null

    init {
        viewModelScope.launch {
            val accounts = accountRepository.getAll().first()
            val defaultId = accounts.find { it.isDefault }?.id
            _state.update { it.copy(accounts = accounts, selectedAccountId = defaultId) }
            startCollecting()
        }
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            DashboardAction.Refresh -> {
                collectJob?.cancel()
                startCollecting()
            }

            is DashboardAction.AccountSelected -> {
                _state.update { it.copy(selectedAccountId = action.accountId) }
                collectJob?.cancel()
                startCollecting()
            }

            DashboardAction.PreviousMonth -> {
                val s = _state.value
                val y = if (s.selectedMonth == 1) s.selectedYear - 1 else s.selectedYear
                val m = if (s.selectedMonth == 1) 12 else s.selectedMonth - 1
                _state.update { it.copy(selectedYear = y, selectedMonth = m, isLoading = true) }
                collectJob?.cancel()
                startCollecting()
            }

            DashboardAction.NextMonth -> {
                val s = _state.value
                val y = if (s.selectedMonth == 12) s.selectedYear + 1 else s.selectedYear
                val m = if (s.selectedMonth == 12) 1 else s.selectedMonth + 1
                _state.update { it.copy(selectedYear = y, selectedMonth = m, isLoading = true) }
                collectJob?.cancel()
                startCollecting()
            }
        }
    }

    private fun startCollecting() {
        val s = _state.value
        logger.d { "Loading dashboard: year=${s.selectedYear} month=${s.selectedMonth} account=${s.selectedAccountId}" }
        collectJob = viewModelScope.launch {
            safeRunCatching {
                combine(
                    getDashboardSummary(s.selectedAccountId, s.selectedYear, s.selectedMonth),
                    categoryRepository.getAll(),
                ) { summary, cats ->
                    _state.value.copy(
                        isLoading = false,
                        summary = summary.copy(
                            recentTransactions = summary.recentTransactions.mapCategories(cats)
                        ),
                    )
                }.collect { _state.value = it }
            }.onFailure { e ->
                logger.e(e) { "Failed to load dashboard" }
                _state.update { it.copy(isLoading = false) }
                _events.send(DashboardEvent.ShowError("Erro ao carregar painel"))
            }
        }
    }
}
