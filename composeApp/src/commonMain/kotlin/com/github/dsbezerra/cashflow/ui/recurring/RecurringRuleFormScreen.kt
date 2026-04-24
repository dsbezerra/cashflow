package com.github.dsbezerra.cashflow.ui.recurring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.dsbezerra.cashflow.domain.model.CategoryType
import com.github.dsbezerra.cashflow.domain.model.Frequency
import com.github.dsbezerra.cashflow.domain.model.TransactionType
import com.github.dsbezerra.cashflow.ui.common.DesktopVerticalScrollbar
import com.github.dsbezerra.cashflow.util.formatPtBr
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringRuleFormScreen(
    ruleId: String?,
    onNavigateBack: () -> Unit,
    viewModel: RecurringRuleFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(ruleId) {
        viewModel.initialize(ruleId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                RecurringRuleFormEvent.NavigateBack -> onNavigateBack()
                is RecurringRuleFormEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Editar Regra Recorrente" else "Nova Regra Recorrente") },
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
                // Type
                val types = listOf(TransactionType.INCOME, TransactionType.EXPENSE)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    types.forEachIndexed { index, type ->
                        SegmentedButton(
                            selected = state.type == type,
                            onClick = { viewModel.onAction(RecurringRuleFormAction.TypeChanged(type)) },
                            shape = SegmentedButtonDefaults.itemShape(index, types.size),
                            label = {
                                Text(
                                    when (type) {
                                        TransactionType.INCOME -> "Receita"
                                        else -> "Despesa"
                                    }
                                )
                            },
                        )
                    }
                }

                // Description
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.onAction(RecurringRuleFormAction.DescriptionChanged(it)) },
                    label = { Text("Descrição") },
                    isError = state.descriptionError != null,
                    supportingText = state.descriptionError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Amount
                OutlinedTextField(
                    value = state.amountInput,
                    onValueChange = { viewModel.onAction(RecurringRuleFormAction.AmountChanged(it)) },
                    label = { Text("Valor") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = state.amountError != null,
                    supportingText = state.amountError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Account
                RuleDropdown(
                    label = "Conta",
                    selectedId = state.selectedAccountId,
                    items = state.availableAccounts.map { it.id to it.name },
                    isError = state.accountError != null,
                    errorText = state.accountError,
                    onSelect = { viewModel.onAction(RecurringRuleFormAction.AccountSelected(it)) },
                )

                // Category (filtered by type)
                val filteredCategories = state.availableCategories.filter { cat ->
                    when (state.type) {
                        TransactionType.INCOME -> cat.type == CategoryType.INCOME || cat.type == CategoryType.BOTH
                        TransactionType.EXPENSE -> cat.type == CategoryType.EXPENSE || cat.type == CategoryType.BOTH
                        else -> true
                    }
                }
                RuleDropdown(
                    label = "Categoria",
                    selectedId = state.selectedCategoryId,
                    items = filteredCategories.map { it.id to it.name },
                    isError = state.categoryError != null,
                    errorText = state.categoryError,
                    onSelect = { viewModel.onAction(RecurringRuleFormAction.CategorySelected(it)) },
                )

                // Frequency
                RuleStringDropdown(
                    label = "Frequência",
                    selectedValue = state.frequency.labelPtBr(),
                    options = Frequency.entries.map { it.labelPtBr() },
                    onSelect = { selected ->
                        val freq = Frequency.entries.first { it.labelPtBr() == selected }
                        viewModel.onAction(RecurringRuleFormAction.FrequencyChanged(freq))
                    },
                )

                // Interval
                OutlinedTextField(
                    value = state.interval.toString(),
                    onValueChange = { v ->
                        v.toIntOrNull()?.let { viewModel.onAction(RecurringRuleFormAction.IntervalChanged(it)) }
                    },
                    label = { Text("Intervalo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )

                // Start date
                val startLocalDate = if (state.startDate > 0L)
                    Instant.fromEpochMilliseconds(state.startDate)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                else null

                OutlinedButton(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Início: ${startLocalDate?.formatPtBr() ?: "Selecionar"}")
                }

                // End date
                val endLocalDate = state.endDate?.let {
                    Instant.fromEpochMilliseconds(it)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                }

                OutlinedButton(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Fim: ${endLocalDate?.formatPtBr() ?: "Sem data final"}")
                }

                // Active toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Ativo", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = state.isActive,
                        onCheckedChange = { viewModel.onAction(RecurringRuleFormAction.ActiveChanged(it)) },
                    )
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.onAction(RecurringRuleFormAction.Save) },
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (state.isSaving) "Salvando..." else "Salvar")
                }
            }
            DesktopVerticalScrollbar(scrollState)
        }
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.startDate.takeIf { it > 0L },
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.onAction(RecurringRuleFormAction.StartDateChanged(it))
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.endDate)
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onAction(RecurringRuleFormAction.EndDateChanged(datePickerState.selectedDateMillis))
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Excluir Regra") },
            text = { Text("Tem certeza que deseja excluir esta regra recorrente?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.onAction(RecurringRuleFormAction.ConfirmDelete)
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") }
            },
        )
    }
}

private fun Frequency.labelPtBr(): String = when (this) {
    Frequency.DAILY -> "Diário"
    Frequency.WEEKLY -> "Semanal"
    Frequency.MONTHLY -> "Mensal"
    Frequency.YEARLY -> "Anual"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RuleDropdown(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RuleStringDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
