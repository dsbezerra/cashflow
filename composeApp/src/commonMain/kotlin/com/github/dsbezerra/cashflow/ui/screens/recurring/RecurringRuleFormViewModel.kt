package com.github.dsbezerra.cashflow.ui.screens.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.domain.model.RecurringRule
import com.github.dsbezerra.cashflow.domain.model.toDecimal
import com.github.dsbezerra.cashflow.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.domain.repository.CategoryRepository
import com.github.dsbezerra.cashflow.domain.repository.RecurringRuleRepository
import com.github.dsbezerra.cashflow.util.generateId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class RecurringRuleFormViewModel(
    private val recurringRuleRepository: RecurringRuleRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormState())
    val state = _state.asStateFlow()

    private val _events = Channel<com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun initialize(ruleId: String?) {
        viewModelScope.launch {
            val accounts = accountRepository.getAll().first()
            val categories = categoryRepository.getAll().first()
            val now = Clock.System.now().toEpochMilliseconds()

            if (ruleId != null) {
                val rule = recurringRuleRepository.getById(ruleId)
                if (rule != null) {
                    _state.update {
                        it.copy(
                            ruleId = rule.id,
                            isEditMode = true,
                            isLoading = false,
                            description = rule.description,
                            amountInput = rule.amount.toString(),
                            type = rule.type,
                            selectedAccountId = rule.accountId,
                            selectedCategoryId = rule.categoryId,
                            frequency = rule.frequency,
                            interval = rule.interval,
                            startDate = rule.startDate,
                            endDate = rule.endDate,
                            isActive = rule.isActive,
                            availableAccounts = accounts,
                            availableCategories = categories,
                        )
                    }
                    return@launch
                }
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    startDate = now,
                    availableAccounts = accounts,
                    availableCategories = categories,
                )
            }
        }
    }

    fun onAction(action: com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction) {
        when (action) {
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction.DescriptionChanged -> _state.update { it.copy(description = action.description, descriptionError = null) }
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction.AmountChanged -> _state.update { it.copy(amountInput = action.amount, amountError = null) }
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction.TypeChanged -> _state.update { it.copy(type = action.type, selectedCategoryId = null) }
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction.AccountSelected -> _state.update { it.copy(selectedAccountId = action.accountId, accountError = null) }
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction.CategorySelected -> _state.update { it.copy(selectedCategoryId = action.categoryId, categoryError = null) }
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction.FrequencyChanged -> _state.update { it.copy(frequency = action.frequency) }
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction.IntervalChanged -> _state.update { it.copy(interval = action.interval) }
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction.StartDateChanged -> _state.update { it.copy(startDate = action.epochMillis) }
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction.EndDateChanged -> _state.update { it.copy(endDate = action.epochMillis) }
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction.ActiveChanged -> _state.update { it.copy(isActive = action.isActive) }
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction.Save -> save()
            is com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormAction.ConfirmDelete -> delete()
        }
    }

    private fun save() {
        val s = _state.value
        var hasError = false

        val amount = s.amountInput.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _state.update { it.copy(amountError = "Informe um valor válido") }
            hasError = true
        }
        if (s.description.isBlank()) {
            _state.update { it.copy(descriptionError = "Descrição é obrigatória") }
            hasError = true
        }
        if (s.selectedAccountId == null) {
            _state.update { it.copy(accountError = "Selecione uma conta") }
            hasError = true
        }
        if (s.selectedCategoryId == null) {
            _state.update { it.copy(categoryError = "Selecione uma categoria") }
            hasError = true
        }
        if (hasError) return

        _state.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            runCatching {
                if (s.isEditMode && s.ruleId != null) {
                    val existing = recurringRuleRepository.getById(s.ruleId) ?: return@runCatching
                    recurringRuleRepository.update(
                        existing.copy(
                            description = s.description,
                            amount = amount?.toDecimal()!!,
                            type = s.type,
                            accountId = s.selectedAccountId!!,
                            categoryId = s.selectedCategoryId!!,
                            frequency = s.frequency,
                            interval = s.interval,
                            startDate = s.startDate,
                            endDate = s.endDate,
                            isActive = s.isActive,
                        )
                    )
                } else {
                    recurringRuleRepository.insert(
                        RecurringRule(
                            id = generateId(),
                            accountId = s.selectedAccountId!!,
                            categoryId = s.selectedCategoryId!!,
                            type = s.type,
                            amount = amount?.toDecimal()!!,
                            description = s.description,
                            frequency = s.frequency,
                            interval = s.interval,
                            startDate = s.startDate,
                            endDate = s.endDate,
                            nextOccurrence = s.startDate,
                            isActive = s.isActive,
                        )
                    )
                }
            }.onSuccess {
                _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormEvent.NavigateBack)
            }.onFailure {
                _state.update { st -> st.copy(isSaving = false) }
                _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormEvent.ShowError("Erro ao salvar regra"))
            }
        }
    }

    private fun delete() {
        val id = _state.value.ruleId ?: return
        viewModelScope.launch {
            runCatching { recurringRuleRepository.delete(id) }
                .onSuccess { _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormEvent.NavigateBack) }
                .onFailure { _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormEvent.ShowError("Erro ao excluir regra")) }
        }
    }
}
