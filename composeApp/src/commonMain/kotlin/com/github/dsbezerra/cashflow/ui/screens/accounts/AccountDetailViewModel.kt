package com.github.dsbezerra.cashflow.ui.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.domain.model.TransactionType
import com.github.dsbezerra.cashflow.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.domain.repository.TransactionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountDetailViewModel(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailState())
    val state = _state.asStateFlow()

    private val _events = Channel<com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var collectJob: Job? = null

    fun initialize(accountId: String) {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            combine(
                accountRepository.getAll().map { list -> list.find { it.id == accountId } },
                transactionRepository.getByAccount(accountId),
            ) { account, transactions ->
                val balance = if (account != null) {
                    val net = transactions.sumOf { tx ->
                        when (tx.type) {
                            TransactionType.INCOME -> tx.amount.toDouble()
                            TransactionType.EXPENSE -> -tx.amount.toDouble()
                            TransactionType.TRANSFER -> 0.0
                        }
                    }
                    account.initialBalance.toDouble() + net
                } else 0.0
                Triple(account, balance, transactions.sortedByDescending { it.date })
            }.collect { (account, balance, transactions) ->
                _state.update {
                    it.copy(
                        account = account,
                        balance = balance,
                        transactions = transactions,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onAction(action: com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailAction) {
        when (action) {
            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailAction.ConfirmDelete -> delete()
            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailAction.ConfirmArchive -> archive()
        }
    }

    private fun delete() {
        val id = _state.value.account?.id ?: return
        viewModelScope.launch {
            runCatching { accountRepository.delete(id) }
                .onSuccess { _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailEvent.NavigateBack) }
                .onFailure { _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailEvent.ShowError("Falha ao excluir conta")) }
        }
    }

    private fun archive() {
        val account = _state.value.account ?: return
        viewModelScope.launch {
            runCatching { accountRepository.update(account.copy(isArchived = true)) }
                .onSuccess { _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailEvent.NavigateBack) }
                .onFailure { _events.send(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailEvent.ShowError("Falha ao arquivar conta")) }
        }
    }
}
