package com.github.dsbezerra.cashflow.feature.report

sealed interface ReportEvent {
    data class ShowError(val message: String) :
        ReportEvent
}
