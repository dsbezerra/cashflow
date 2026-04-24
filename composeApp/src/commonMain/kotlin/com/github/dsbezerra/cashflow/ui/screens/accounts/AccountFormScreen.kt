package com.github.dsbezerra.cashflow.ui.screens.accounts

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.github.dsbezerra.cashflow.domain.model.AccountType
import com.github.dsbezerra.cashflow.ui.common.accountIconOptions
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

private val currencies = listOf("BRL", "USD", "EUR", "GBP")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountFormScreen(
    accountId: String?,
    onNavigateBack: () -> Unit,
    viewModel: com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(accountId) {
        viewModel.initialize(accountId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormEvent.NavigateBack -> onNavigateBack()
                is com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Editar Conta" else "Nova Conta") },
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
            // Name
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.NameChanged(it)) },
                label = { Text("Nome") },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )

            // Type
            Text("Tipo", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                AccountType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = state.type == type,
                        onClick = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.TypeChanged(type)) },
                        shape = SegmentedButtonDefaults.itemShape(index, AccountType.entries.size),
                        label = { Text(
                            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.accountTypeName(
                                type
                            ), maxLines = 1) },
                    )
                }
            }

            // Currency
                _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.SimpleDropdown(
                    label = "Moeda",
                    selectedValue = state.currency,
                    options = _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.currencies,
                    onSelect = {
                        viewModel.onAction(
                            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.CurrencyChanged(
                                it
                            )
                        )
                    },
                )

            // Initial balance
            OutlinedTextField(
                value = state.initialBalanceInput,
                onValueChange = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.InitialBalanceChanged(it)) },
                label = { Text("Saldo Inicial") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.initialBalanceError != null,
                supportingText = state.initialBalanceError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )

            // Icon
                _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.SimpleDropdown(
                    label = "Ícone",
                    selectedValue = state.icon,
                    options = accountIconOptions,
                    onSelect = {
                        viewModel.onAction(
                            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.IconChanged(
                                it
                            )
                        )
                    },
                )

            // Color
            OutlinedTextField(
                value = state.color,
                onValueChange = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.ColorChanged(it)) },
                label = { Text("Cor (hex)") },
                placeholder = { Text("#4CAF50") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isSaving) "Salvando..." else "Salvar")
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
                    Text("Excluir Conta")
                }
            }
            }
            DesktopVerticalScrollbar(scrollState)
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Excluir Conta") },
            text = { Text("Tem certeza que deseja excluir esta conta?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormAction.ConfirmDelete)
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
