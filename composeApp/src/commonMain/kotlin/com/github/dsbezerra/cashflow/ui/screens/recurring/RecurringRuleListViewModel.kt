package com.github.dsbezerra.cashflow.ui.screens.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.domain.repository.RecurringRuleRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecurringRuleListViewModel(
    private val recurringRuleRepository: RecurringRuleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListState())
    val state = _state.asStateFlow()

    private val _events = Channel<com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            recurringRuleRepository.getAll().collect { rules ->
                _state.update { it.copy(rules = rules, isLoading = false) }
            }
        }
    }

    fun onAction(action: com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListAction) {
        when (action) {
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListAction.ToggleActive -> toggle(action.id, action.isActive)
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListAction.Delete -> delete(action.id)
        }
    }

    private fun toggle(id: String, isActive: Boolean) {
        viewModelScope.launch {
            runCatching {
                val rule = recurringRuleRepository.getById(id) ?: return@runCatching
                recurringRuleRepository.update(rule.copy(isActive = isActive))
            }.onFailure {
                _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListEvent.ShowError("Erro ao atualizar regra"))
            }
        }
    }

    private fun delete(id: String) {
        viewModelScope.launch {
            runCatching { recurringRuleRepository.delete(id) }
                .onFailure { _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListEvent.ShowError("Erro ao excluir regra")) }
        }
    }
}
