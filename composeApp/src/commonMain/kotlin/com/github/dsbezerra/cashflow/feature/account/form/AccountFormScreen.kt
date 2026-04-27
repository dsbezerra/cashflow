package com.github.dsbezerra.cashflow.feature.account.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import com.github.dsbezerra.cashflow.core.designsystem.component.CurrencyTextField
import androidx.compose.foundation.verticalScroll
import com.github.dsbezerra.cashflow.core.designsystem.component.DesktopVerticalScrollbar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import com.github.dsbezerra.cashflow.core.designsystem.component.IconButtonWithTooltip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.account_color_placeholder
import cashflow.composeapp.generated.resources.account_currency_label
import cashflow.composeapp.generated.resources.account_delete
import cashflow.composeapp.generated.resources.account_delete_confirm
import cashflow.composeapp.generated.resources.account_edit
import cashflow.composeapp.generated.resources.account_initial_balance
import cashflow.composeapp.generated.resources.account_new
import cashflow.composeapp.generated.resources.back
import cashflow.composeapp.generated.resources.cancel
import cashflow.composeapp.generated.resources.color_hex_label
import cashflow.composeapp.generated.resources.delete
import cashflow.composeapp.generated.resources.icon_label
import cashflow.composeapp.generated.resources.name_label
import cashflow.composeapp.generated.resources.save
import cashflow.composeapp.generated.resources.saving
import cashflow.composeapp.generated.resources.type_label
import com.github.dsbezerra.cashflow.core.domain.model.AccountType
import com.github.dsbezerra.cashflow.core.designsystem.component.accountIconOptions
import com.github.dsbezerra.cashflow.feature.account.accountTypeName
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val currencies = listOf("BRL", "USD", "EUR", "GBP")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountFormScreen(
    accountId: String?,
    onNavigateBack: () -> Unit,
    viewModel: AccountFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(accountId) {
        viewModel.initialize(accountId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AccountFormEvent.NavigateBack -> onNavigateBack()
                is AccountFormEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) stringResource(Res.string.account_edit) else stringResource(Res.string.account_new)) },
                navigationIcon = {
                    IconButtonWithTooltip(onClick = onNavigateBack, tooltip = stringResource(Res.string.back)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        AccountFormBody(
            state = state,
            onAction = viewModel::onAction,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountFormSheet(
    onDismiss: () -> Unit,
    viewModel: AccountFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initialize(null)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AccountFormEvent.NavigateBack -> onDismiss()
                is AccountFormEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Box {
            AccountFormBody(state = state, onAction = viewModel::onAction)
            SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountFormBody(
    state: AccountFormState,
    onAction: (AccountFormAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Name
            OutlinedTextField(
                value = state.name,
                onValueChange = { onAction(AccountFormAction.NameChanged(it)) },
                label = { Text(stringResource(Res.string.name_label)) },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )

            // Type
            Text(stringResource(Res.string.type_label), style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                AccountType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = state.type == type,
                        onClick = { onAction(AccountFormAction.TypeChanged(type)) },
                        shape = SegmentedButtonDefaults.itemShape(index, AccountType.entries.size),
                        label = { Text(accountTypeName(type), maxLines = 1) },
                    )
                }
            }

            // Currency
            SimpleDropdown(
                label = stringResource(Res.string.account_currency_label),
                selectedValue = state.currency,
                options = currencies,
                onSelect = { onAction(AccountFormAction.CurrencyChanged(it)) },
            )

            // Initial balance
            CurrencyTextField(
                value = state.initialBalanceInCents,
                onValueChange = { onAction(AccountFormAction.InitialBalanceChanged(it)) },
                label = stringResource(Res.string.account_initial_balance),
                isError = state.initialBalanceError != null,
                supportingText = state.initialBalanceError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )

            // Icon
            SimpleDropdown(
                label = stringResource(Res.string.icon_label),
                selectedValue = state.icon,
                options = accountIconOptions,
                onSelect = { onAction(AccountFormAction.IconChanged(it)) },
            )

            // Color
            OutlinedTextField(
                value = state.color,
                onValueChange = { onAction(AccountFormAction.ColorChanged(it)) },
                label = { Text(stringResource(Res.string.color_hex_label)) },
                placeholder = { Text(stringResource(Res.string.account_color_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onAction(AccountFormAction.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isSaving) stringResource(Res.string.saving) else stringResource(Res.string.save))
            }

            if (state.isEditMode) {
                Button(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(Res.string.account_delete))
                }
            }

            Spacer(Modifier.height(8.dp))
        }
        DesktopVerticalScrollbar(scrollState)
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(Res.string.account_delete)) },
            text = { Text(stringResource(Res.string.account_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onAction(AccountFormAction.ConfirmDelete)
                }) { Text(stringResource(Res.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text(stringResource(Res.string.cancel)) }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDropdown(
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
