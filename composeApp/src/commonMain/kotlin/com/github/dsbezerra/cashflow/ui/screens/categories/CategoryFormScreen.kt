package com.github.dsbezerra.cashflow.ui.screens.categories

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
import androidx.compose.foundation.verticalScroll
import com.github.dsbezerra.cashflow.ui.common.DesktopVerticalScrollbar
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.dsbezerra.cashflow.domain.model.CategoryType
import com.github.dsbezerra.cashflow.domain.model.DreClassification
import com.github.dsbezerra.cashflow.ui.common.categoryIconOptions
import com.github.dsbezerra.cashflow.ui.screens.categories.labelPtBr
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    categoryId: String?,
    onNavigateBack: () -> Unit,
    viewModel: com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(categoryId) {
        viewModel.initialize(categoryId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormEvent.NavigateBack -> onNavigateBack()
                is com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Editar Categoria" else "Nova Categoria") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (state.isEditMode && !state.isDefault) {
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
                onValueChange = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction.NameChanged(it)) },
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
                        onClick = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction.TypeChanged(type)) },
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
                _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryDropdown(
                    label = "Classificação DRE",
                    selectedValue = state.dreClassification.labelPtBr(),
                    options = DreClassification.entries.map { it.labelPtBr() },
                    onSelect = { selected ->
                        val dre = DreClassification.entries.first { it.labelPtBr() == selected }
                        viewModel.onAction(
                            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction.DreClassificationChanged(
                                dre
                            )
                        )
                    },
                )

            // Icon
                _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryDropdown(
                    label = "Ícone",
                    selectedValue = state.icon,
                    options = categoryIconOptions,
                    onSelect = {
                        viewModel.onAction(
                            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction.IconChanged(
                                it
                            )
                        )
                    },
                )

            // Color
            OutlinedTextField(
                value = state.color,
                onValueChange = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction.ColorChanged(it)) },
                label = { Text("Cor (hex)") },
                placeholder = { Text("#9E9E9E") },
                modifier = Modifier.fillMaxWidth(),
            )

            // Parent category
            if (state.availableParents.isNotEmpty()) {
                val parentOptions = listOf(null to "Sem categoria pai") +
                        state.availableParents.map { it.id to it.name }
                _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryDropdown(
                    label = "Categoria pai (opcional)",
                    selectedValue = parentOptions.firstOrNull { it.first == state.parentId }?.second
                        ?: "Sem categoria pai",
                    options = parentOptions.map { it.second },
                    onSelect = { selected ->
                        val parentId = parentOptions.firstOrNull { it.second == selected }?.first
                        viewModel.onAction(
                            _root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction.ParentChanged(
                                parentId
                            )
                        )
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
                        onCheckedChange = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction.ArchivedChanged(it)) },
                    )
                    Text("Arquivar categoria", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction.Save) },
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
            }
            DesktopVerticalScrollbar(scrollState)
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Excluir Categoria") },
            text = { Text("Tem certeza que deseja excluir esta categoria?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.onAction(_root_ide_package_.com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction.ConfirmDelete)
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
