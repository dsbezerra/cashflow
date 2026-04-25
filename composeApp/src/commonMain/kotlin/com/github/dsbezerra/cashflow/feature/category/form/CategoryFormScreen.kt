package com.github.dsbezerra.cashflow.feature.category.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.github.dsbezerra.cashflow.core.designsystem.component.DesktopVerticalScrollbar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import com.github.dsbezerra.cashflow.core.domain.model.CategoryType
import com.github.dsbezerra.cashflow.core.domain.model.DreClassification
import com.github.dsbezerra.cashflow.core.designsystem.component.categoryIconOptions
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    categoryId: String?,
    onNavigateBack: () -> Unit,
    viewModel: CategoryFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(categoryId) {
        viewModel.initialize(categoryId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                CategoryFormEvent.NavigateBack -> onNavigateBack()
                is CategoryFormEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Editar Categoria" else "Nova Categoria") },
                navigationIcon = {
                    IconButtonWithTooltip(onClick = onNavigateBack, tooltip = "Voltar") {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (state.isEditMode && !state.isDefault) {
                        IconButtonWithTooltip(
                            onClick = { viewModel.onAction(CategoryFormAction.ConfirmDelete) },
                            tooltip = "Excluir",
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir")
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        CategoryFormBody(
            state = state,
            onAction = viewModel::onAction,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormSheet(
    onDismiss: () -> Unit,
    viewModel: CategoryFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initialize(null)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                CategoryFormEvent.NavigateBack -> onDismiss()
                is CategoryFormEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Box {
            CategoryFormBody(state = state, onAction = viewModel::onAction)
            SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormBody(
    state: CategoryFormState,
    onAction: (CategoryFormAction) -> Unit,
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
                onValueChange = { onAction(CategoryFormAction.NameChanged(it)) },
                label = { Text("Nome") },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isDefault,
            )

            // Type
            Text("Tipo", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                CategoryType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = state.type == type,
                        onClick = { onAction(CategoryFormAction.TypeChanged(type)) },
                        shape = SegmentedButtonDefaults.itemShape(index, CategoryType.entries.size),
                        enabled = !state.isDefault,
                        label = {
                            Text(
                                when (type) {
                                    CategoryType.INCOME -> "Receita"
                                    CategoryType.EXPENSE -> "Despesa"
                                    CategoryType.BOTH -> "Ambos"
                                }
                            )
                        },
                    )
                }
            }

            // DRE Classification
            Text("Classificação DRE", style = MaterialTheme.typography.labelLarge)
            CategoryDropdown(
                label = "Classificação DRE",
                selectedValue = state.dreClassification.labelPtBr(),
                options = DreClassification.entries.map { it.labelPtBr() },
                onSelect = { selected ->
                    val dre = DreClassification.entries.first { it.labelPtBr() == selected }
                    onAction(CategoryFormAction.DreClassificationChanged(dre))
                },
            )

            // Icon
            CategoryDropdown(
                label = "Ícone",
                selectedValue = state.icon,
                options = categoryIconOptions,
                onSelect = { onAction(CategoryFormAction.IconChanged(it)) },
            )

            // Color
            OutlinedTextField(
                value = state.color,
                onValueChange = { onAction(CategoryFormAction.ColorChanged(it)) },
                label = { Text("Cor (hex)") },
                placeholder = { Text("#9E9E9E") },
                modifier = Modifier.fillMaxWidth(),
            )

            // Parent category
            if (state.availableParents.isNotEmpty()) {
                val parentOptions = listOf(null to "Sem categoria pai") +
                        state.availableParents.map { it.id to it.name }
                CategoryDropdown(
                    label = "Categoria pai (opcional)",
                    selectedValue = parentOptions.firstOrNull { it.first == state.parentId }?.second
                        ?: "Sem categoria pai",
                    options = parentOptions.map { it.second },
                    onSelect = { selected ->
                        val parentId = parentOptions.firstOrNull { it.second == selected }?.first
                        onAction(CategoryFormAction.ParentChanged(parentId))
                    },
                )
            }

            // Archive toggle (edit mode only)
            if (state.isEditMode) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Checkbox(
                        checked = state.isArchived,
                        onCheckedChange = { onAction(CategoryFormAction.ArchivedChanged(it)) },
                    )
                    Text("Arquivar categoria", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onAction(CategoryFormAction.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isSaving) "Salvando..." else "Salvar")
            }

            if (state.isEditMode && !state.isDefault) {
                Button(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Excluir Categoria")
                }
            }

            Spacer(Modifier.height(8.dp))
        }
        DesktopVerticalScrollbar(scrollState)
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Excluir Categoria") },
            text = { Text("Tem certeza que deseja excluir esta categoria?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onAction(CategoryFormAction.ConfirmDelete)
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") }
            },
        )
    }
}

private fun DreClassification.labelPtBr(): String = when (this) {
    DreClassification.GROSS_REVENUE -> "Receita Bruta"
    DreClassification.DEDUCTION -> "Deduções"
    DreClassification.COST -> "Custos"
    DreClassification.EXPENSE -> "Despesas Operacionais"
    DreClassification.NONE -> "Não classificado"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
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
