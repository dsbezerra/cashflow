package com.github.dsbezerra.cashflow.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.domain.usecase.report.GetDreReportUseCase
import com.github.dsbezerra.cashflow.domain.usecase.report.GetReportUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ReportViewModel(
    private val getReport: GetReportUseCase,
    private val getDreReport: GetDreReportUseCase,
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

    init {
        startCollecting()
        startDreCollecting()
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
            }
        }
    }

    private fun startCollecting() {
        collectJob = viewModelScope.launch {
            runCatching {
                getReport(_state.value.selectedPeriod).collect { data ->
                    _state.update { it.copy(isLoading = false, data = data) }
                }
            }.onFailure {
                _events.send(ReportEvent.ShowError("Erro ao carregar relatório"))
            }
        }
    }

    private fun startDreCollecting() {
        dreCollectJob = viewModelScope.launch {
            runCatching {
                val s = _state.value
                getDreReport(s.dreYear, s.dreMonth).collect { report ->
                    _state.update { it.copy(isDreLoading = false, dreReport = report) }
                }
            }.onFailure {
                _events.send(ReportEvent.ShowError("Erro ao carregar DRE"))
            }
        }
    }
}
