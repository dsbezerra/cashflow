package com.github.dsbezerra.cashflow.ui.screens.dashboard

sealed interface DashboardEvent {
    data class ShowError(val message: String) :
        DashboardEvent
}
