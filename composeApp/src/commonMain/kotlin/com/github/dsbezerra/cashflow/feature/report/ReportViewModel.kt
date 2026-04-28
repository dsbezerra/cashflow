package com.github.dsbezerra.cashflow.feature.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.core.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.core.domain.usecase.report.GetDreReportUseCase
import com.github.dsbezerra.cashflow.core.domain.usecase.report.GetReportUseCase
import com.github.dsbezerra.cashflow.util.safeRunCatching
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ReportViewModel(
    private val getReport: GetReportUseCase,
    private val getDreReport: GetDreReportUseCase,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(run {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        ReportState(dreYear = now.year, dreMonth = now.monthNumber)
    })
    val state = _state.asStateFlow()

    private val _events = Channel<ReportEvent>()
    val events = _events.receiveAsFlow()

    private var collectJob: Job? = null
    private var dreCollectJob: Job? = null
    private var dreComparisonCollectJob: Job? = null

    init {
        viewModelScope.launch {
            val accounts = accountRepository.getAll().first()
            val defaultId = accounts.find { it.isDefault }?.id
            _state.update { it.copy(accounts = accounts, selectedAccountId = defaultId) }
            startCollecting()
            startDreCollecting()
        }
    }

    fun onAction(action: ReportAction) {
        when (action) {
            is ReportAction.PeriodChanged -> {
                _state.update { it.copy(selectedPeriod = action.period, isLoading = true) }
                collectJob?.cancel()
                startCollecting()
            }
            is ReportAction.TabChanged -> {
                _state.update { it.copy(selectedTab = action.tab) }
            }
            is ReportAction.DreMonthChanged -> {
                _state.update { it.copy(dreYear = action.year, dreMonth = action.month, isDreLoading = true) }
                dreCollectJob?.cancel()
                startDreCollecting()
                if (_state.value.showDreComparison) {
                    dreComparisonCollectJob?.cancel()
                    startDreComparisonCollecting()
                }
            }
            is ReportAction.AccountSelected -> {
                _state.update { it.copy(selectedAccountId = action.accountId, isLoading = true, isDreLoading = true) }
                collectJob?.cancel()
                dreCollectJob?.cancel()
                startCollecting()
                startDreCollecting()
                if (_state.value.showDreComparison) {
                    dreComparisonCollectJob?.cancel()
                    startDreComparisonCollecting()
                }
            }
            is ReportAction.ToggleDreComparison -> {
                val showing = !_state.value.showDreComparison
                _state.update { it.copy(showDreComparison = showing, isDreComparisonLoading = showing) }
                if (showing) {
                    dreComparisonCollectJob?.cancel()
                    startDreComparisonCollecting()
                } else {
                    dreComparisonCollectJob?.cancel()
                    _state.update { it.copy(comparisonDreReport = null) }
                }
            }
        }
    }

    private fun startCollecting() {
        val s = _state.value
        collectJob = viewModelScope.launch {
            safeRunCatching {
                getReport(s.selectedPeriod, s.selectedAccountId).collect { data ->
                    _state.update { it.copy(isLoading = false, data = data) }
                }
            }.onFailure {
                _events.send(ReportEvent.ShowError("Erro ao carregar relatório"))
            }
        }
    }

    private fun startDreCollecting() {
        val s = _state.value
        dreCollectJob = viewModelScope.launch {
            safeRunCatching {
                getDreReport(s.dreYear, s.dreMonth, s.selectedAccountId).collect { report ->
                    _state.update { it.copy(isDreLoading = false, dreReport = report) }
                }
            }.onFailure {
                _events.send(ReportEvent.ShowError("Erro ao carregar DRE"))
            }
        }
    }

    private fun startDreComparisonCollecting() {
        val s = _state.value
        val prevMonth = if (s.dreMonth == 1) 12 else s.dreMonth - 1
        val prevYear = if (s.dreMonth == 1) s.dreYear - 1 else s.dreYear
        dreComparisonCollectJob = viewModelScope.launch {
            safeRunCatching {
                getDreReport(prevYear, prevMonth, s.selectedAccountId).collect { report ->
                    _state.update { it.copy(isDreComparisonLoading = false, comparisonDreReport = report) }
                }
            }.onFailure {
                _state.update { it.copy(isDreComparisonLoading = false) }
                _events.send(ReportEvent.ShowError("Erro ao carregar comparação DRE"))
            }
        }
    }
}
