package com.github.dsbezerra.cashflow.feature.report

import com.github.dsbezerra.cashflow.core.domain.model.Account
import com.github.dsbezerra.cashflow.core.domain.model.DreReport
import com.github.dsbezerra.cashflow.core.domain.model.ReportData
import com.github.dsbezerra.cashflow.core.domain.model.ReportPeriod

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
    // Account filter
    val accounts: List<Account> = emptyList(),
    val selectedAccountId: String? = null,
)

enum class ReportTab { BY_PERIOD, BY_CATEGORY, DRE }
