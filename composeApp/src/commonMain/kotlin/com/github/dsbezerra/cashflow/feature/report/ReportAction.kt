package com.github.dsbezerra.cashflow.feature.report

import com.github.dsbezerra.cashflow.core.domain.model.ReportPeriod

sealed interface ReportAction {
    data class PeriodChanged(val period: ReportPeriod) : ReportAction
    data class TabChanged(val tab: ReportTab) : ReportAction
    data class DreMonthChanged(val year: Int, val month: Int) : ReportAction
    data class AccountSelected(val accountId: String?) : ReportAction
}
