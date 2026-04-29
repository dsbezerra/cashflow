package com.github.dsbezerra.cashflow.feature.recurring.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.github.dsbezerra.cashflow.core.domain.repository.RecurringRuleRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import com.github.dsbezerra.cashflow.util.safeRunCatching
import kotlinx.coroutines.launch

class RecurringRuleListViewModel(
    private val recurringRuleRepository: RecurringRuleRepository,
    private val logger: Logger,
) : ViewModel() {

    private val _state = MutableStateFlow(RecurringRuleListState())
    val state = _state.asStateFlow()

    private val _events = Channel<RecurringRuleListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            recurringRuleRepository.getAll().collect { rules ->
                _state.update { it.copy(rules = rules, isLoading = false) }
            }
        }
    }

    fun onAction(action: RecurringRuleListAction) {
        when (action) {
            is RecurringRuleListAction.ToggleActive -> toggle(action.id, action.isActive)
            is RecurringRuleListAction.Delete -> delete(action.id)
        }
    }

    private fun toggle(id: String, isActive: Boolean) {
        viewModelScope.launch {
            safeRunCatching {
                val rule = recurringRuleRepository.getById(id) ?: return@safeRunCatching
                recurringRuleRepository.update(rule.copy(isActive = isActive))
            }.onFailure { e ->
                logger.e(e) { "Failed to toggle recurring rule: id=$id isActive=$isActive" }
                _events.send(RecurringRuleListEvent.ShowError("Erro ao atualizar regra"))
            }
        }
    }

    private fun delete(id: String) {
        logger.d { "Deleting recurring rule: id=$id" }
        viewModelScope.launch {
            safeRunCatching { recurringRuleRepository.delete(id) }
                .onFailure { e ->
                    logger.e(e) { "Failed to delete recurring rule: id=$id" }
                    _events.send(RecurringRuleListEvent.ShowError("Erro ao excluir regra"))
                }
        }
    }
}
