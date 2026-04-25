package com.github.dsbezerra.cashflow.feature.transaction.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import com.github.dsbezerra.cashflow.core.designsystem.component.ChipSelector
import com.github.dsbezerra.cashflow.core.designsystem.component.CurrencyTextField
import androidx.compose.foundation.verticalScroll
import com.github.dsbezerra.cashflow.core.designsystem.component.DesktopVerticalScrollbar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import com.github.dsbezerra.cashflow.core.designsystem.component.IconButtonWithTooltip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import com.github.dsbezerra.cashflow.core.domain.model.CategoryType
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
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
    viewModel: TransactionDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(transactionId) {
        viewModel.initialize(transactionId, defaultAccountId, defaultType)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is TransactionDetailEvent.NavigateBack -> onNavigateBack()
                is TransactionDetailEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Editar Transação" else "Nova Transação") },
                navigationIcon = {
                    IconButtonWithTooltip(onClick = onNavigateBack, tooltip = "Voltar") {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        TransactionFormBody(
            state = state,
            onAction = viewModel::onAction,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormSheet(
    onDismiss: () -> Unit,
    viewModel: TransactionDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initialize(null)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is TransactionDetailEvent.NavigateBack -> onDismiss()
                is TransactionDetailEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Box {
            TransactionFormBody(state = state, onAction = viewModel::onAction)
            SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormBody(
    state: TransactionDetailState,
    onAction: (TransactionDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
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
            // Type selector
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                TransactionType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = state.type == type,
                        onClick = { onAction(TransactionDetailAction.TypeChanged(type)) },
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
            CurrencyTextField(
                value = state.amountInCents,
                onValueChange = { onAction(TransactionDetailAction.AmountChanged(it)) },
                label = "Valor",
                isError = state.amountError != null,
                supportingText = state.amountError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )

            // Description
            OutlinedTextField(
                value = state.description,
                onValueChange = { onAction(TransactionDetailAction.DescriptionChanged(it)) },
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
                ChipSelector(
                    label = "Categoria",
                    selectedId = state.selectedCategoryId,
                    items = filteredCategories.map { it.id to it.name },
                    isError = state.categoryError != null,
                    errorText = state.categoryError,
                    onSelect = { onAction(TransactionDetailAction.CategorySelected(it)) },
                )
            }

            // Account
            ChipSelector(
                label = if (state.type == TransactionType.TRANSFER) "Conta de Origem" else "Conta",
                selectedId = state.selectedAccountId,
                items = state.accounts.map { it.id to it.name },
                isError = state.accountError != null,
                errorText = state.accountError,
                onSelect = { onAction(TransactionDetailAction.AccountSelected(it)) },
            )

            // To Account (TRANSFER only)
            if (state.type == TransactionType.TRANSFER) {
                ChipSelector(
                    label = "Conta de Destino",
                    selectedId = state.selectedToAccountId,
                    items = state.accounts.map { it.id to it.name },
                    onSelect = { onAction(TransactionDetailAction.ToAccountSelected(it)) },
                )
            }

            // Notes
            OutlinedTextField(
                value = state.notes,
                onValueChange = { onAction(TransactionDetailAction.NotesChanged(it)) },
                label = { Text("Notas (opcional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onAction(TransactionDetailAction.Save) },
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
                    Text("Excluir Transação")
                }
            }

            Spacer(Modifier.height(8.dp))
        }
        DesktopVerticalScrollbar(scrollState)
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onAction(TransactionDetailAction.DateChanged(it))
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
                    onAction(TransactionDetailAction.ConfirmDelete)
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") }
            },
        )
    }
}
