package com.github.dsbezerra.cashflow.ui.screens.reports

sealed interface ReportEvent {
    data class ShowError(val message: String) :
        ReportEvent
}
