package com.github.dsbezerra.cashflow.feature.scenario.calculator

import com.github.dsbezerra.cashflow.core.domain.model.Account
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType

data class ScenarioCalculatorState(
    val isLoading: Boolean = true,
    val accounts: List<Account> = emptyList(),
    val selectedAccountId: String? = null,
    val currentBalance: Double = 0.0,
    val type: TransactionType = TransactionType.INCOME,
    val amountPerItemCents: Long = 0L,
    val quantity: Int = 1,
    val projectedBalance: Double = 0.0,
    val difference: Double = 0.0,
)
