package com.github.dsbezerra.cashflow.feature.account.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.core.domain.model.Account
import com.github.dsbezerra.cashflow.core.domain.model.toDecimal
import com.github.dsbezerra.cashflow.core.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.util.generateId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.time.Clock

class AccountFormViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AccountFormState())
    val state = _state.asStateFlow()

    private val _events = Channel<AccountFormEvent>(Channel.BUFFERED)
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
                    initialBalanceInCents = (account.initialBalance.toDouble() * 100).roundToLong(),
                    color = account.color,
                    icon = account.icon,
                    isArchived = account.isArchived,
                    isEditMode = true,
                )
            }
        }
    }

    fun onAction(action: AccountFormAction) {
        when (action) {
            is AccountFormAction.NameChanged -> _state.update { it.copy(name = action.name, nameError = null) }
            is AccountFormAction.TypeChanged -> _state.update { it.copy(type = action.type) }
            is AccountFormAction.CurrencyChanged -> _state.update { it.copy(currency = action.currency) }
            is AccountFormAction.InitialBalanceChanged -> _state.update { it.copy(initialBalanceInCents = action.cents, initialBalanceError = null) }
            is AccountFormAction.ColorChanged -> _state.update { it.copy(color = action.color) }
            is AccountFormAction.IconChanged -> _state.update { it.copy(icon = action.icon) }
            AccountFormAction.Save -> save()
            AccountFormAction.ConfirmDelete -> delete()
        }
    }

    private fun save() {
        val s = _state.value
        var hasError = false

        if (s.name.isBlank()) {
            _state.update { it.copy(nameError = "Nome é obrigatório") }
            hasError = true
        }
        val balance = s.initialBalanceInCents / 100.0
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
                            initialBalance = balance.toDecimal(),
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
                            initialBalance = balance.toDecimal(),
                            color = s.color,
                            icon = s.icon,
                            isArchived = false,
                            createdAt = now,
                        )
                    )
                }
            }.onSuccess {
                _events.send(AccountFormEvent.NavigateBack)
            }.onFailure {
                _state.update { st -> st.copy(isSaving = false) }
                _events.send(AccountFormEvent.ShowError("Falha ao salvar conta"))
            }
        }
    }

    private fun delete() {
        val id = _state.value.accountId ?: return
        viewModelScope.launch {
            runCatching { accountRepository.delete(id) }
                .onSuccess { _events.send(AccountFormEvent.NavigateBack) }
                .onFailure { _events.send(AccountFormEvent.ShowError("Falha ao excluir conta")) }
        }
    }
}
