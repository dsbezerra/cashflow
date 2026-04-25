package com.github.dsbezerra.cashflow.feature.dashboard

sealed interface DashboardEvent {
    data class ShowError(val message: String) :
        DashboardEvent
}
