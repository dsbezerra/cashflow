package com.github.dsbezerra.cashflow.core.domain.model

import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.report_period_last_3_months
import cashflow.composeapp.generated.resources.report_period_last_6_months
import cashflow.composeapp.generated.resources.report_period_last_month
import cashflow.composeapp.generated.resources.report_period_this_month
import cashflow.composeapp.generated.resources.report_period_this_year
import org.jetbrains.compose.resources.StringResource

enum class ReportPeriod(val res: StringResource) {
    THIS_MONTH(Res.string.report_period_this_month),
    LAST_MONTH(Res.string.report_period_last_month),
    LAST_3_MONTHS(Res.string.report_period_last_3_months),
    LAST_6_MONTHS(Res.string.report_period_last_6_months),
    THIS_YEAR(Res.string.report_period_this_year),
}
