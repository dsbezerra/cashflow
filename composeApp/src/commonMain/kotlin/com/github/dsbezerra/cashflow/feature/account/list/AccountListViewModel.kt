package com.github.dsbezerra.cashflow.feature.account.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.core.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.core.domain.usecase.account.GetAccountBalanceUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountListViewModel(
    private val accountRepository: AccountRepository,
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AccountListState())
    val state = _state.asStateFlow()

    private val _events = Channel<AccountListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var collectJob: Job? = null

    init {
        startCollecting()
    }

    fun onAction(action: AccountListAction) {
        when (action) {
            AccountListAction.Refresh -> {
                collectJob?.cancel()
                startCollecting()
            }
            is AccountListAction.SetDefault -> viewModelScope.launch {
                runCatching { accountRepository.setDefault(action.accountId) }
                    .onFailure { _events.send(AccountListEvent.ShowError("Falha ao definir conta padrão")) }
            }
        }
    }

    private fun startCollecting() {
        collectJob = viewModelScope.launch {
            accountRepository.getAll().collect { accounts ->
                val visible = accounts.filter { !it.isArchived }
                val withBalances = mutableListOf<AccountWithBalance>()
                for (account in visible) {
                    val balance = runCatching { getAccountBalanceUseCase(account) }
                        .getOrDefault(account.initialBalance)
                    withBalances.add(AccountWithBalance(account, balance))
                }
                _state.update { it.copy(accounts = withBalances, isLoading = false) }
            }
        }
    }
}
