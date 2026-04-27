package com.github.dsbezerra.cashflow.feature.recurring.form

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
import com.github.dsbezerra.cashflow.core.designsystem.component.ChipSelector
import com.github.dsbezerra.cashflow.core.designsystem.component.CurrencyTextField
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
import com.github.dsbezerra.cashflow.core.designsystem.component.IconButtonWithTooltip
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
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.account_label
import cashflow.composeapp.generated.resources.amount_label
import cashflow.composeapp.generated.resources.back
import cashflow.composeapp.generated.resources.cancel
import cashflow.composeapp.generated.resources.category_label
import cashflow.composeapp.generated.resources.delete
import cashflow.composeapp.generated.resources.description_label
import cashflow.composeapp.generated.resources.expense
import cashflow.composeapp.generated.resources.income
import cashflow.composeapp.generated.resources.ok
import cashflow.composeapp.generated.resources.recurring_active
import cashflow.composeapp.generated.resources.recurring_delete_confirm
import cashflow.composeapp.generated.resources.recurring_delete_title
import cashflow.composeapp.generated.resources.recurring_edit
import cashflow.composeapp.generated.resources.recurring_end_date
import cashflow.composeapp.generated.resources.recurring_freq_daily
import cashflow.composeapp.generated.resources.recurring_freq_monthly
import cashflow.composeapp.generated.resources.recurring_freq_weekly
import cashflow.composeapp.generated.resources.recurring_freq_yearly
import cashflow.composeapp.generated.resources.recurring_frequency_label
import cashflow.composeapp.generated.resources.recurring_interval_label
import cashflow.composeapp.generated.resources.recurring_new
import cashflow.composeapp.generated.resources.recurring_no_end_date
import cashflow.composeapp.generated.resources.recurring_select
import cashflow.composeapp.generated.resources.recurring_start_date
import cashflow.composeapp.generated.resources.save
import cashflow.composeapp.generated.resources.saving
import com.github.dsbezerra.cashflow.core.domain.model.CategoryType
import com.github.dsbezerra.cashflow.core.domain.model.Frequency
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
import com.github.dsbezerra.cashflow.core.designsystem.component.DesktopVerticalScrollbar
import com.github.dsbezerra.cashflow.util.formatPtBr
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
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
                title = { Text(if (state.isEditMode) stringResource(Res.string.recurring_edit) else stringResource(Res.string.recurring_new)) },
                navigationIcon = {
                    IconButtonWithTooltip(onClick = onNavigateBack, tooltip = stringResource(Res.string.back)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                    }
                },
                actions = {
                    if (state.isEditMode) {
                        IconButtonWithTooltip(onClick = { showDeleteConfirm = true }, tooltip = stringResource(Res.string.delete)) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(Res.string.delete))
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
                                        TransactionType.INCOME -> stringResource(Res.string.income)
                                        else -> stringResource(Res.string.expense)
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
                    label = { Text(stringResource(Res.string.description_label)) },
                    isError = state.descriptionError != null,
                    supportingText = state.descriptionError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Amount
                CurrencyTextField(
                    value = state.amountInCents,
                    onValueChange = { viewModel.onAction(RecurringRuleFormAction.AmountChanged(it)) },
                    label = stringResource(Res.string.amount_label),
                    isError = state.amountError != null,
                    supportingText = state.amountError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Account
                ChipSelector(
                    label = stringResource(Res.string.account_label),
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
                ChipSelector(
                    label = stringResource(Res.string.category_label),
                    selectedId = state.selectedCategoryId,
                    items = filteredCategories.map { it.id to it.name },
                    isError = state.categoryError != null,
                    errorText = state.categoryError,
                    onSelect = { viewModel.onAction(RecurringRuleFormAction.CategorySelected(it)) },
                )

                ChipSelector(
                    label = stringResource(Res.string.recurring_frequency_label),
                    selectedId = state.frequency.name,
                    items = Frequency.entries.map { freq ->
                        freq.name to when (freq) {
                            Frequency.DAILY -> stringResource(Res.string.recurring_freq_daily)
                            Frequency.WEEKLY -> stringResource(Res.string.recurring_freq_weekly)
                            Frequency.MONTHLY -> stringResource(Res.string.recurring_freq_monthly)
                            Frequency.YEARLY -> stringResource(Res.string.recurring_freq_yearly)
                        }
                    },
                    onSelect = { selected ->
                        val freq = Frequency.entries.first { it.name == selected }
                        viewModel.onAction(RecurringRuleFormAction.FrequencyChanged(freq))
                    },
                )

                // Interval
                OutlinedTextField(
                    value = state.interval.toString(),
                    onValueChange = { v ->
                        v.toIntOrNull()?.let { viewModel.onAction(RecurringRuleFormAction.IntervalChanged(it)) }
                    },
                    label = { Text(stringResource(Res.string.recurring_interval_label)) },
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
                    Text(stringResource(Res.string.recurring_start_date, startLocalDate?.formatPtBr() ?: stringResource(Res.string.recurring_select)))
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
                    Text(stringResource(Res.string.recurring_end_date, endLocalDate?.formatPtBr() ?: stringResource(Res.string.recurring_no_end_date)))
                }

                // Active toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(Res.string.recurring_active), style = MaterialTheme.typography.bodyLarge)
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
                    Text(if (state.isSaving) stringResource(Res.string.saving) else stringResource(Res.string.save))
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
                }) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text(stringResource(Res.string.cancel)) }
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
                }) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text(stringResource(Res.string.cancel)) }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(Res.string.recurring_delete_title)) },
            text = { Text(stringResource(Res.string.recurring_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.onAction(RecurringRuleFormAction.ConfirmDelete)
                }) { Text(stringResource(Res.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text(stringResource(Res.string.cancel)) }
            },
        )
    }
}