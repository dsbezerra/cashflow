package com.github.dsbezerra.cashflow.feature.scenario.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
import com.github.dsbezerra.cashflow.core.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.core.domain.usecase.account.GetAccountBalanceUseCase
import com.github.dsbezerra.cashflow.util.safeRunCatching
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScenarioCalculatorViewModel(
    private val accountRepository: AccountRepository,
    private val getAccountBalance: GetAccountBalanceUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ScenarioCalculatorState())
    val state = _state.asStateFlow()

    private val _events = Channel<ScenarioCalculatorEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            safeRunCatching {
                val accounts = accountRepository.getAll().first()
                val default = accounts.find { it.isDefault } ?: accounts.firstOrNull()
                _state.update {
                    it.copy(
                        accounts = accounts,
                        selectedAccountId = default?.id,
                        isLoading = false,
                    )
                }
                if (default != null) {
                    loadBalance(default.id)
                }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
                _events.send(ScenarioCalculatorEvent.ShowError("Erro ao carregar contas"))
            }
        }
    }

    fun onAction(action: ScenarioCalculatorAction) {
        when (action) {
            is ScenarioCalculatorAction.AccountSelected -> {
                _state.update { it.copy(selectedAccountId = action.accountId) }
                loadBalance(action.accountId)
            }
            is ScenarioCalculatorAction.TypeChanged -> {
                _state.update { it.copy(type = action.type) }
                recalculate()
            }
            is ScenarioCalculatorAction.AmountChanged -> {
                _state.update { it.copy(amountPerItemCents = action.cents) }
                recalculate()
            }
            is ScenarioCalculatorAction.QuantityChanged -> {
                _state.update { it.copy(quantity = action.quantity.coerceAtLeast(1)) }
                recalculate()
            }
        }
    }

    private fun loadBalance(accountId: String) {
        viewModelScope.launch {
            safeRunCatching {
                val account = accountRepository.getById(accountId) ?: return@safeRunCatching
                val balance = getAccountBalance(account).toDouble()
                _state.update { it.copy(currentBalance = balance) }
                recalculate()
            }.onFailure {
                _events.send(ScenarioCalculatorEvent.ShowError("Erro ao carregar saldo"))
            }
        }
    }

    private fun recalculate() {
        val s = _state.value
        val amountPerItem = s.amountPerItemCents / 100.0
        val impact = amountPerItem * s.quantity
        val projected = when (s.type) {
            TransactionType.INCOME -> s.currentBalance + impact
            TransactionType.EXPENSE -> s.currentBalance - impact
            TransactionType.TRANSFER -> s.currentBalance
        }
        _state.update { it.copy(projectedBalance = projected, difference = projected - s.currentBalance) }
    }
}
