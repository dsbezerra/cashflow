package com.github.dsbezerra.cashflow.domain.usecase.budget

import com.github.dsbezerra.cashflow.domain.model.BudgetStatus

class CheckBudgetThresholdUseCase {
    operator fun invoke(currentSpend: Double, budgetAmount: Double): BudgetStatus {
        if (budgetAmount <= 0.0) return BudgetStatus.OK
        val ratio = currentSpend / budgetAmount
        return when {
            ratio >= 1.0 -> BudgetStatus.EXCEEDED
            ratio >= 0.8 -> BudgetStatus.WARNING
            else -> BudgetStatus.OK
        }
    }
}
