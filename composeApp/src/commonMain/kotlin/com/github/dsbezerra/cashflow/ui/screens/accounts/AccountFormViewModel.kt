package com.github.dsbezerra.cashflow.ui.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.domain.model.Account
import com.github.dsbezerra.cashflow.domain.model.toDecimal
import com.github.dsbezerra.cashflow.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.util.generateId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class AccountFormViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormState())
    val state = _state.asStateFlow()

    private val _events = Channel<com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun initialize(accountId: String?) {
        if (accountId == null) return
        viewModelScope.launch {
            val account = accountRepository.getById(accountId) ?: return@launch
            _state.update {
                it.copy(
                    accountId = account.id,
                    name = account.name,
                    type = account.type,
                    currency = account.currency,
                    initialBalanceInput = account.initialBalance.toString(),
                    color = account.color,
                    icon = account.icon,
                    isArchived = account.isArchived,
                    isEditMode = true,
                )
            }
        }
    }

    fun onAction(action: com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction) {
        when (action) {
            is com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.NameChanged -> _state.update { it.copy(name = action.name, nameError = null) }
            is com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.TypeChanged -> _state.update { it.copy(type = action.type) }
            is com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.CurrencyChanged -> _state.update { it.copy(currency = action.currency) }
            is com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.InitialBalanceChanged -> _state.update { it.copy(initialBalanceInput = action.value, initialBalanceError = null) }
            is com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.ColorChanged -> _state.update { it.copy(color = action.color) }
            is com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.IconChanged -> _state.update { it.copy(icon = action.icon) }
            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.Save -> save()
            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.ConfirmDelete -> delete()
        }
    }

    private fun save() {
        val s = _state.value
        var hasError = false

        if (s.name.isBlank()) {
            _state.update { it.copy(nameError = "Nome é obrigatório") }
            hasError = true
        }
        val balance = s.initialBalanceInput.toDoubleOrNull()
        if (balance == null) {
            _state.update { it.copy(initialBalanceError = "Saldo inicial inválido") }
            hasError = true
        }
        if (hasError) return

        _state.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            runCatching {
                val now = Clock.System.now().toEpochMilliseconds()
                if (s.isEditMode) {
                    val existing = accountRepository.getById(s.accountId!!)!!
                    accountRepository.update(
                        existing.copy(
                            name = s.name,
                            type = s.type,
                            currency = s.currency,
                            initialBalance = balance?.toDecimal()!!,
                            color = s.color,
                            icon = s.icon,
                            isArchived = s.isArchived,
                        )
                    )
                } else {
                    accountRepository.insert(
                        Account(
                            id = generateId(),
                            name = s.name,
                            type = s.type,
                            currency = s.currency,
                            initialBalance = balance?.toDecimal()!!,
                            color = s.color,
                            icon = s.icon,
                            isArchived = false,
                            createdAt = now,
                        )
                    )
                }
            }.onSuccess {
                _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormEvent.NavigateBack)
            }.onFailure {
                _state.update { st -> st.copy(isSaving = false) }
                _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormEvent.ShowError("Falha ao salvar conta"))
            }
        }
    }

    private fun delete() {
        val id = _state.value.accountId ?: return
        viewModelScope.launch {
            runCatching { accountRepository.delete(id) }
                .onSuccess { _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormEvent.NavigateBack) }
                .onFailure { _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormEvent.ShowError("Falha ao excluir conta")) }
        }
    }
}
