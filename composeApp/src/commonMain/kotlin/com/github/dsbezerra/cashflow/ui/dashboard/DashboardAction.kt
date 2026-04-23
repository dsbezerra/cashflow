package com.github.dsbezerra.cashflow.ui.dashboard

sealed interface DashboardAction {
    data object Refresh : DashboardAction
}
