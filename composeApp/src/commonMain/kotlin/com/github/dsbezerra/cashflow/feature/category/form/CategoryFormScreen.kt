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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import cashflow.composeapp.generated.resources.back
import cashflow.composeapp.generated.resources.cancel
import cashflow.composeapp.generated.resources.category_archive
import cashflow.composeapp.generated.resources.category_both_short
import cashflow.composeapp.generated.resources.category_delete
import cashflow.composeapp.generated.resources.category_delete_confirm
import cashflow.composeapp.generated.resources.category_dre_classification
import cashflow.composeapp.generated.resources.category_edit
import cashflow.composeapp.generated.resources.category_new
import cashflow.composeapp.generated.resources.category_no_parent
import cashflow.composeapp.generated.resources.category_parent_label
import cashflow.composeapp.generated.resources.delete
import cashflow.composeapp.generated.resources.dre_costs
import cashflow.composeapp.generated.resources.dre_deductions
import cashflow.composeapp.generated.resources.dre_gross_revenue
import cashflow.composeapp.generated.resources.dre_operational_expenses
import cashflow.composeapp.generated.resources.dre_unclassified
import cashflow.composeapp.generated.resources.expense
import cashflow.composeapp.generated.resources.icon_label
import cashflow.composeapp.generated.resources.income
import cashflow.composeapp.generated.resources.name_label
import cashflow.composeapp.generated.resources.save
import cashflow.composeapp.generated.resources.saving
import cashflow.composeapp.generated.resources.type_label
import com.github.dsbezerra.cashflow.core.domain.model.CategoryType
import com.github.dsbezerra.cashflow.core.domain.model.DreClassification
import com.github.dsbezerra.cashflow.core.designsystem.component.ChipSelector
import com.github.dsbezerra.cashflow.core.designsystem.component.categoryIcon
import com.github.dsbezerra.cashflow.core.designsystem.component.categoryIconOptions
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
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
                title = { Text(if (state.isEditMode) stringResource(Res.string.category_edit) else stringResource(Res.string.category_new)) },
                navigationIcon = {
                    IconButtonWithTooltip(onClick = onNavigateBack, tooltip = stringResource(Res.string.back)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                    }
                },
                actions = {
                    if (state.isEditMode && !state.isDefault) {
                        IconButtonWithTooltip(
                            onClick = { viewModel.onAction(CategoryFormAction.ConfirmDelete) },
                            tooltip = stringResource(Res.string.delete),
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(Res.string.delete))
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
                label = { Text(stringResource(Res.string.name_label)) },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isDefault,
            )

            // Type
            Text(stringResource(Res.string.type_label), style = MaterialTheme.typography.labelLarge)
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
                                    CategoryType.INCOME -> stringResource(Res.string.income)
                                    CategoryType.EXPENSE -> stringResource(Res.string.expense)
                                    CategoryType.BOTH -> stringResource(Res.string.category_both_short)
                                }
                            )
                        },
                    )
                }
            }

            // DRE Classification
            val dreItems = DreClassification.entries.map { dre ->
                dre.name to when (dre) {
                    DreClassification.GROSS_REVENUE -> stringResource(Res.string.dre_gross_revenue)
                    DreClassification.DEDUCTION -> stringResource(Res.string.dre_deductions)
                    DreClassification.COST -> stringResource(Res.string.dre_costs)
                    DreClassification.EXPENSE -> stringResource(Res.string.dre_operational_expenses)
                    DreClassification.NONE -> stringResource(Res.string.dre_unclassified)
                }
            }
            ChipSelector(
                label = stringResource(Res.string.category_dre_classification),
                selectedId = state.dreClassification.name,
                items = dreItems,
                onSelect = { onAction(CategoryFormAction.DreClassificationChanged(DreClassification.valueOf(it))) },
            )

            // Icon
            ChipSelector(
                label = stringResource(Res.string.icon_label),
                selectedId = state.icon,
                items = categoryIconOptions.map { it to "" },
                leadingIcon = { categoryIcon(it) },
                onSelect = { onAction(CategoryFormAction.IconChanged(it)) },
            )

            // Parent category
            if (state.availableParents.isNotEmpty()) {
                val noParentLabel = stringResource(Res.string.category_no_parent)
                ChipSelector(
                    label = stringResource(Res.string.category_parent_label),
                    selectedId = state.parentId ?: "",
                    items = listOf("" to noParentLabel) +
                            state.availableParents.map { it.id to it.name },
                    onSelect = { onAction(CategoryFormAction.ParentChanged(it.takeIf { id -> id.isNotEmpty() })) },
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
                    Text(stringResource(Res.string.category_archive), style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onAction(CategoryFormAction.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isSaving) stringResource(Res.string.saving) else stringResource(Res.string.save))
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
                    Text(stringResource(Res.string.category_delete))
                }
            }

            Spacer(Modifier.height(8.dp))
        }
        DesktopVerticalScrollbar(scrollState)
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(Res.string.category_delete)) },
            text = { Text(stringResource(Res.string.category_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onAction(CategoryFormAction.ConfirmDelete)
                }) { Text(stringResource(Res.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text(stringResource(Res.string.cancel)) }
            },
        )
    }
}

