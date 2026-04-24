package com.github.dsbezerra.cashflow.ui.screens.dashboard

sealed interface DashboardAction {
    data object Refresh : DashboardAction
    data class AccountSelected(val accountId: String?) : DashboardAction
    data object PreviousMonth : DashboardAction
    data object NextMonth : DashboardAction
}
