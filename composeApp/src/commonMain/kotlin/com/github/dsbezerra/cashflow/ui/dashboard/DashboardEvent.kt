package com.github.dsbezerra.cashflow.ui.dashboard

sealed interface DashboardEvent {
    data class ShowError(val message: String) : DashboardEvent
}
