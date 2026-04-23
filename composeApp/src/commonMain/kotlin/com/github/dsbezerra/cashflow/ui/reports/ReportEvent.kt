package com.github.dsbezerra.cashflow.ui.reports

sealed interface ReportEvent {
    data class ShowError(val message: String) : ReportEvent
}
