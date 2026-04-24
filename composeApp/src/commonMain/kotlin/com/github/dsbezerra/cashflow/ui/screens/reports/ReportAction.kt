package com.github.dsbezerra.cashflow.ui.screens.reports

import com.github.dsbezerra.cashflow.domain.model.ReportPeriod

sealed interface ReportAction {
    data class PeriodChanged(val period: ReportPeriod) : ReportAction
    data class TabChanged(val tab: ReportTab) : ReportAction
    data class DreMonthChanged(val year: Int, val month: Int) : ReportAction
    data class AccountSelected(val accountId: String?) : ReportAction
}
