package com.github.dsbezerra.cashflow.feature.recurring.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.github.dsbezerra.cashflow.core.domain.model.RecurringRule
import com.github.dsbezerra.cashflow.core.domain.model.toDecimal
import com.github.dsbezerra.cashflow.core.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.core.domain.repository.CategoryRepository
import com.github.dsbezerra.cashflow.core.domain.repository.RecurringRuleRepository
import com.github.dsbezerra.cashflow.util.generateId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import com.github.dsbezerra.cashflow.util.safeRunCatching
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.time.Clock

class RecurringRuleFormViewModel(
    private val recurringRuleRepository: RecurringRuleRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val logger: Logger,
) : ViewModel() {

    private val _state = MutableStateFlow(RecurringRuleFormState())
    val state = _state.asStateFlow()

    private val _events = Channel<RecurringRuleFormEvent>(Channel.BUFFERED)
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
                            amountInCents = (rule.amount.toDouble() * 100).roundToLong(),
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

    fun onAction(action: RecurringRuleFormAction) {
        when (action) {
            is RecurringRuleFormAction.DescriptionChanged -> _state.update { it.copy(description = action.description, descriptionError = null) }
            is RecurringRuleFormAction.AmountChanged -> _state.update { it.copy(amountInCents = action.cents, amountError = null) }
            is RecurringRuleFormAction.TypeChanged -> _state.update { it.copy(type = action.type, selectedCategoryId = null) }
            is RecurringRuleFormAction.AccountSelected -> _state.update { it.copy(selectedAccountId = action.accountId, accountError = null) }
            is RecurringRuleFormAction.CategorySelected -> _state.update { it.copy(selectedCategoryId = action.categoryId, categoryError = null) }
            is RecurringRuleFormAction.FrequencyChanged -> _state.update { it.copy(frequency = action.frequency) }
            is RecurringRuleFormAction.IntervalChanged -> _state.update { it.copy(interval = action.interval) }
            is RecurringRuleFormAction.StartDateChanged -> _state.update { it.copy(startDate = action.epochMillis) }
            is RecurringRuleFormAction.EndDateChanged -> _state.update { it.copy(endDate = action.epochMillis) }
            is RecurringRuleFormAction.ActiveChanged -> _state.update { it.copy(isActive = action.isActive) }
            is RecurringRuleFormAction.Save -> save()
            is RecurringRuleFormAction.ConfirmDelete -> delete()
        }
    }

    private fun save() {
        val s = _state.value
        var hasError = false

        if (s.amountInCents <= 0L) {
            _state.update { it.copy(amountError = "Informe um valor válido") }
            hasError = true
        }
        val amount = s.amountInCents / 100.0
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
        logger.d { "Saving recurring rule: editMode=${s.isEditMode}" }

        viewModelScope.launch {
            safeRunCatching {
                if (s.isEditMode && s.ruleId != null) {
                    val existing = recurringRuleRepository.getById(s.ruleId) ?: return@safeRunCatching
                    recurringRuleRepository.update(
                        existing.copy(
                            description = s.description,
                            amount = amount.toDecimal(),
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
                            amount = amount.toDecimal(),
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
                _events.send(RecurringRuleFormEvent.NavigateBack)
            }.onFailure { e ->
                logger.e(e) { "Failed to save recurring rule" }
                _state.update { st -> st.copy(isSaving = false) }
                _events.send(RecurringRuleFormEvent.ShowError("Erro ao salvar regra"))
            }
        }
    }

    private fun delete() {
        val id = _state.value.ruleId ?: return
        logger.d { "Deleting recurring rule: id=$id" }
        viewModelScope.launch {
            safeRunCatching { recurringRuleRepository.delete(id) }
                .onSuccess { _events.send(RecurringRuleFormEvent.NavigateBack) }
                .onFailure { e ->
                    logger.e(e) { "Failed to delete recurring rule: id=$id" }
                    _events.send(RecurringRuleFormEvent.ShowError("Erro ao excluir regra"))
                }
        }
    }
}
