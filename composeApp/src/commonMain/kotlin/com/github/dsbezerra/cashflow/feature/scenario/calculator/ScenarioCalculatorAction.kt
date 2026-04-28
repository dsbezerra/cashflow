package com.github.dsbezerra.cashflow.feature.scenario.calculator

import com.github.dsbezerra.cashflow.core.domain.model.TransactionType

sealed interface ScenarioCalculatorAction {
    data class AccountSelected(val accountId: String) : ScenarioCalculatorAction
    data class TypeChanged(val type: TransactionType) : ScenarioCalculatorAction
    data class AmountChanged(val cents: Long) : ScenarioCalculatorAction
    data class QuantityChanged(val quantity: Int) : ScenarioCalculatorAction
}
