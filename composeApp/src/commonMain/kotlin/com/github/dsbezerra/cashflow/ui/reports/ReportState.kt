package com.github.dsbezerra.cashflow.ui.reports

import com.github.dsbezerra.cashflow.domain.model.DreReport
import com.github.dsbezerra.cashflow.domain.model.ReportData
import com.github.dsbezerra.cashflow.domain.model.ReportPeriod

data class ReportState(
    val isLoading: Boolean = true,
    val selectedPeriod: ReportPeriod = ReportPeriod.THIS_MONTH,
    val selectedTab: ReportTab = ReportTab.BY_PERIOD,
    val data: ReportData? = null,
    // DRE tab state
    val dreYear: Int = 0,
    val dreMonth: Int = 0,
    val dreReport: DreReport? = null,
    val isDreLoading: Boolean = false,
)

enum class ReportTab { BY_PERIOD, BY_CATEGORY, DRE }
