package com.github.dsbezerra.cashflow.feature.transaction.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.core.domain.model.Transaction
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
import com.github.dsbezerra.cashflow.core.domain.model.toDecimal
import com.github.dsbezerra.cashflow.core.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.core.domain.repository.CategoryRepository
import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository
import com.github.dsbezerra.cashflow.core.domain.usecase.transaction.CreateTransactionUseCase
import com.github.dsbezerra.cashflow.core.domain.usecase.transaction.CreateTransferUseCase
import com.github.dsbezerra.cashflow.core.domain.usecase.transaction.DeleteTransactionUseCase
import com.github.dsbezerra.cashflow.util.generateId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.time.Clock

class TransactionDetailViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val createTransferUseCase: CreateTransferUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionDetailState())
    val state = _state.asStateFlow()

    private val _events = Channel<TransactionDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun initialize(
        transactionId: String?,
        defaultAccountId: String? = null,
        defaultType: String? = null,
    ) {
        viewModelScope.launch {
            val accounts = accountRepository.getAll().first()
            val categories = categoryRepository.getAll().first()

            if (transactionId != null) {
                val tx = transactionRepository.getById(transactionId)
                if (tx != null) {
                    _state.update {
                        it.copy(
                            transactionId = tx.id,
                            type = tx.type,
                            amountInCents = (tx.amount.toDouble() * 100).roundToLong(),
                            description = tx.description,
                            selectedDate = tx.date,
                            selectedCategoryId = tx.categoryId,
                            selectedAccountId = tx.accountId,
                            notes = tx.notes ?: "",
                            categories = categories,
                            accounts = accounts,
                            isLoading = false,
                            isEditMode = true,
                        )
                    }
                    return@launch
                }
            }

            val prefilledType = defaultType?.let {
                runCatching { TransactionType.valueOf(it) }.getOrNull()
            }
            val resolvedAccountId = defaultAccountId ?: accountRepository.getDefault()?.id
            _state.update {
                it.copy(
                    type = prefilledType ?: it.type,
                    selectedAccountId = resolvedAccountId ?: it.selectedAccountId,
                    categories = categories,
                    accounts = accounts,
                    isLoading = false,
                )
            }
        }
    }

    fun onAction(action: TransactionDetailAction) {
        when (action) {
            is TransactionDetailAction.TypeChanged -> _state.update { it.copy(type = action.type, selectedCategoryId = null) }
            is TransactionDetailAction.AmountChanged -> _state.update { it.copy(amountInCents = action.cents, amountError = null) }
            is TransactionDetailAction.DescriptionChanged -> _state.update { it.copy(description = action.description, descriptionError = null) }
            is TransactionDetailAction.DateChanged -> _state.update { it.copy(selectedDate = action.epochMillis) }
            is TransactionDetailAction.CategorySelected -> _state.update { it.copy(selectedCategoryId = action.categoryId, categoryError = null) }
            is TransactionDetailAction.AccountSelected -> _state.update { it.copy(selectedAccountId = action.accountId, accountError = null) }
            is TransactionDetailAction.ToAccountSelected -> _state.update { it.copy(selectedToAccountId = action.accountId) }
            is TransactionDetailAction.NotesChanged -> _state.update { it.copy(notes = action.notes) }
            is TransactionDetailAction.Save -> save()
            is TransactionDetailAction.Delete, is TransactionDetailAction.ConfirmDelete -> delete()
        }
    }

    private fun save() {
        val s = _state.value
        var hasError = false

        if (s.amountInCents <= 0L) {
            _state.update { it.copy(amountError = "Enter a valid amount") }
            hasError = true
        }
        val amount = s.amountInCents / 100.0
        if (s.description.isBlank()) {
            _state.update { it.copy(descriptionError = "Description is required") }
            hasError = true
        }
        if (s.selectedAccountId == null) {
            _state.update { it.copy(accountError = "Select an account") }
            hasError = true
        }
        if (s.type != TransactionType.TRANSFER && s.selectedCategoryId == null) {
            _state.update { it.copy(categoryError = "Select a category") }
            hasError = true
        }
        if (hasError) return

        _state.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            runCatching {
                val now = Clock.System.now().toEpochMilliseconds()
                when {
                    s.type == TransactionType.TRANSFER -> {
                        createTransferUseCase(
                            fromAccountId = s.selectedAccountId!!,
                            toAccountId = s.selectedToAccountId ?: s.selectedAccountId,
                            amount = amount,
                            description = s.description,
                            date = s.selectedDate,
                            categoryId = s.selectedCategoryId ?: "",
                            notes = s.notes.ifBlank { null },
                        )
                    }
                    s.isEditMode -> {
                        val existing = transactionRepository.getById(s.transactionId!!)!!
                        transactionRepository.update(
                            existing.copy(
                                type = s.type,
                                amount = amount.toDecimal(),
                                description = s.description,
                                date = s.selectedDate,
                                categoryId = s.selectedCategoryId!!,
                                accountId = s.selectedAccountId!!,
                                notes = s.notes.ifBlank { null },
                                updatedAt = now,
                            )
                        )
                    }
                    else -> {
                        createTransactionUseCase(
                            Transaction(
                                id = generateId(),
                                accountId = s.selectedAccountId!!,
                                categoryId = s.selectedCategoryId!!,
                                type = s.type,
                                amount = amount.toDecimal(),
                                description = s.description,
                                date = s.selectedDate,
                                notes = s.notes.ifBlank { null },
                                attachmentPath = null,
                                isRecurring = false,
                                recurringId = null,
                                createdAt = now,
                                updatedAt = now,
                            )
                        )
                    }
                }
            }.onSuccess {
                _events.send(TransactionDetailEvent.NavigateBack)
            }.onFailure {
                _state.update { st -> st.copy(isSaving = false) }
                _events.send(TransactionDetailEvent.ShowError("Failed to save transaction"))
            }
        }
    }

    private fun delete() {
        val id = _state.value.transactionId ?: return
        viewModelScope.launch {
            runCatching { deleteTransactionUseCase(id) }
                .onSuccess { _events.send(TransactionDetailEvent.NavigateBack) }
                .onFailure { _events.send(TransactionDetailEvent.ShowError("Failed to delete transaction")) }
        }
    }
}
