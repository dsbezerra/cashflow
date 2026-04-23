package com.github.dsbezerra.cashflow.ui.reports

import com.github.dsbezerra.cashflow.domain.model.ReportPeriod

sealed interface ReportAction {
    data class PeriodChanged(val period: ReportPeriod) : ReportAction
    data class TabChanged(val tab: ReportTab) : ReportAction
    data class DreMonthChanged(val year: Int, val month: Int) : ReportAction
}
