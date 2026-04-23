package com.github.dsbezerra.cashflow.domain.model

enum class ReportPeriod(val labelPtBr: String) {
    THIS_MONTH("Este mês"),
    LAST_MONTH("Mês anterior"),
    LAST_3_MONTHS("Últimos 3 meses"),
    LAST_6_MONTHS("Últimos 6 meses"),
    THIS_YEAR("Este ano"),
}
