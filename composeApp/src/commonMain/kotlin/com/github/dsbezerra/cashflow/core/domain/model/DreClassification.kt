package com.github.dsbezerra.cashflow.core.domain.model

enum class DreClassification {
    GROSS_REVENUE,     // Receita Bruta
    DEDUCTION,         // Deduções (taxes, returns, discounts)
    COST,              // CMV (Custo das Mercadorias Vendidas)
    EXPENSE,           // Despesas Operacionais
    FINANCIAL_EXPENSE, // Despesas Financeiras (loans, interest)
    NONE,              // Not part of DRE (transfers, savings)
}
