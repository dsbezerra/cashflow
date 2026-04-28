package com.github.dsbezerra.cashflow.feature.scenario.calculator

sealed interface ScenarioCalculatorEvent {
    data object NavigateBack : ScenarioCalculatorEvent
    data class ShowError(val message: String) : ScenarioCalculatorEvent
}
