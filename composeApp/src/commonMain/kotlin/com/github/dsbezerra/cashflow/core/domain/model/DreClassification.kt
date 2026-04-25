package com.github.dsbezerra.cashflow.core.domain.model

enum class DreClassification {
    GROSS_REVENUE,  // Receita Bruta
    DEDUCTION,      // Deduções (taxes, returns, discounts)
    COST,           // Custos (COGS)
    EXPENSE,        // Despesas Operacionais
    NONE,           // Not part of DRE (transfers, savings)
}
