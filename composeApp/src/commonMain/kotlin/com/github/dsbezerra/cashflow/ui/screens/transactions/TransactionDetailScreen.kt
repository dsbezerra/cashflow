package com.github.dsbezerra.cashflow.ui.screens.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import com.github.dsbezerra.cashflow.ui.common.DesktopVerticalScrollbar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.dsbezerra.cashflow.domain.model.Account
import com.github.dsbezerra.cashflow.domain.model.Category
import com.github.dsbezerra.cashflow.domain.model.CategoryType
import com.github.dsbezerra.cashflow.domain.model.TransactionType
import com.github.dsbezerra.cashflow.util.formatPtBr
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String?,
    onNavigateBack: () -> Unit,
    defaultAccountId: String? = null,
    defaultType: String? = null,
    viewModel: com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(transactionId) {
        viewModel.initialize(transactionId, defaultAccountId, defaultType)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailEvent.NavigateBack -> onNavigateBack()
                is com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Editar Transação" else "Nova Transação") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (state.isEditMode) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir")
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
            // Type selector
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                TransactionType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = state.type == type,
                        onClick = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction.TypeChanged(type)) },
                        shape = SegmentedButtonDefaults.itemShape(index, TransactionType.entries.size),
                        label = {
                            Text(
                                when (type) {
                                    TransactionType.INCOME -> "Receita"
                                    TransactionType.EXPENSE -> "Despesa"
                                    TransactionType.TRANSFER -> "Transferência"
                                }
                            )
                        },
                    )
                }
            }

            // Amount
            OutlinedTextField(
                value = state.amountInput,
                onValueChange = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction.AmountChanged(it)) },
                label = { Text("Valor") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.amountError != null,
                supportingText = state.amountError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )

            // Description
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction.DescriptionChanged(it)) },
                label = { Text("Descrição") },
                isError = state.descriptionError != null,
                supportingText = state.descriptionError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )

            // Date
            val localDate = Instant.fromEpochMilliseconds(state.selectedDate)
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Data: ${localDate.formatPtBr()}")
            }

            // Category (hidden for TRANSFER)
            if (state.type != TransactionType.TRANSFER) {
                val filteredCategories = state.categories.filter { cat ->
                    when (state.type) {
                        TransactionType.INCOME -> cat.type == CategoryType.INCOME || cat.type == CategoryType.BOTH
                        TransactionType.EXPENSE -> cat.type == CategoryType.EXPENSE || cat.type == CategoryType.BOTH
                        else -> true
                    }
                }
                _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.AccountOrCategoryDropdown(
                    label = "Categoria",
                    selectedId = state.selectedCategoryId,
                    items = filteredCategories.map { it.id to it.name },
                    isError = state.categoryError != null,
                    errorText = state.categoryError,
                    onSelect = {
                        viewModel.onAction(
                            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction.CategorySelected(
                                it
                            )
                        )
                    },
                )
            }

            // Account
                _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.AccountOrCategoryDropdown(
                    label = if (state.type == TransactionType.TRANSFER) "Conta de Origem" else "Conta",
                    selectedId = state.selectedAccountId,
                    items = state.accounts.map { it.id to it.name },
                    isError = state.accountError != null,
                    errorText = state.accountError,
                    onSelect = {
                        viewModel.onAction(
                            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction.AccountSelected(
                                it
                            )
                        )
                    },
                )

            // To Account (TRANSFER only)
            if (state.type == TransactionType.TRANSFER) {
                _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.AccountOrCategoryDropdown(
                    label = "Conta de Destino",
                    selectedId = state.selectedToAccountId,
                    items = state.accounts.map { it.id to it.name },
                    isError = false,
                    errorText = null,
                    onSelect = {
                        viewModel.onAction(
                            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction.ToAccountSelected(
                                it
                            )
                        )
                    },
                )
            }

            // Notes
            OutlinedTextField(
                value = state.notes,
                onValueChange = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction.NotesChanged(it)) },
                label = { Text("Notas (opcional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isSaving) "Salvando..." else "Salvar")
            }
            }
            DesktopVerticalScrollbar(scrollState)
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction.DateChanged(it))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Excluir Transação") },
            text = { Text("Tem certeza que deseja excluir esta transação?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailAction.ConfirmDelete)
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountOrCategoryDropdown(
    label: String,
    selectedId: String?,
    items: List<Pair<String, String>>,
    isError: Boolean,
    errorText: String?,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = items.firstOrNull { it.first == selectedId }?.second ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            isError = isError,
            supportingText = errorText?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { (id, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onSelect(id)
                        expanded = false
                    },
                )
            }
        }
    }
}
