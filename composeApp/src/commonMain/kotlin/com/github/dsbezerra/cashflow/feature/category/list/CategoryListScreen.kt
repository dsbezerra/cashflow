package com.github.dsbezerra.cashflow.feature.category.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.github.dsbezerra.cashflow.core.designsystem.component.DesktopVerticalScrollbar
import com.github.dsbezerra.cashflow.core.designsystem.component.GroupedListItem
import com.github.dsbezerra.cashflow.core.designsystem.component.groupedItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import com.github.dsbezerra.cashflow.core.designsystem.component.IconButtonWithTooltip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
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
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.back
import cashflow.composeapp.generated.resources.categories_title
import cashflow.composeapp.generated.resources.category_archived
import cashflow.composeapp.generated.resources.category_both
import cashflow.composeapp.generated.resources.category_default
import cashflow.composeapp.generated.resources.category_empty_subtitle
import cashflow.composeapp.generated.resources.category_empty_title
import cashflow.composeapp.generated.resources.category_expenses
import cashflow.composeapp.generated.resources.category_income
import cashflow.composeapp.generated.resources.category_new
import com.github.dsbezerra.cashflow.core.domain.model.Category
import com.github.dsbezerra.cashflow.core.domain.model.CategoryType
import com.github.dsbezerra.cashflow.core.designsystem.component.categoryIcon
import com.github.dsbezerra.cashflow.core.designsystem.component.DSFullscreenLoader
import com.github.dsbezerra.cashflow.feature.category.form.CategoryFormSheet
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToForm: (String?) -> Unit,
    viewModel: CategoryListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCreateSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CategoryListEvent.ShowError -> snackbarHostState.showSnackbar(
                    event.message
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.categories_title)) },
                navigationIcon = {
                    IconButtonWithTooltip(onClick = onNavigateBack, tooltip = stringResource(Res.string.back)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.category_new))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        if (showCreateSheet) {
            CategoryFormSheet(onDismiss = { showCreateSheet = false })
        }
        val listState = rememberLazyListState()
        if (state.isLoading) {
            DSFullscreenLoader(
                innerPadding = innerPadding
            )
        } else if (state.active.isEmpty() && state.archived.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(Res.string.category_empty_title), style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(Res.string.category_empty_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            val grouped = state.active.groupBy { it.type }
            val typeOrder = listOf(CategoryType.INCOME, CategoryType.EXPENSE, CategoryType.BOTH)

            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 56.dp),
                ) {
                    typeOrder.forEach { type ->
                        val items = grouped[type] ?: return@forEach
                        item(key = "header-${type.name}") {
                            TypeSectionHeader(type)
                        }
                        groupedItems(items, key = { it.id }) { category, position ->
                            GroupedListItem(position = position) {
                                CategoryRow(
                                    category = category,
                                    onClick = { onNavigateToForm(category.id) },
                                )
                            }
                        }
                    }

                    if (state.archived.isNotEmpty()) {
                        item(key = "header-archived") {
                            TypeSectionHeader(null)
                        }
                        groupedItems(state.archived, key = { "archived-${it.id}" }) { category, position ->
                            GroupedListItem(position = position) {
                                CategoryRow(
                                    category = category,
                                    onClick = { onNavigateToForm(category.id) },
                                )
                            }
                        }
                    }
                }
                DesktopVerticalScrollbar(listState)
            }
        }
    }
}

@Composable
private fun TypeSectionHeader(type: CategoryType?) {
    Text(
        text = when (type) {
            CategoryType.INCOME -> stringResource(Res.string.category_income)
            CategoryType.EXPENSE -> stringResource(Res.string.category_expenses)
            CategoryType.BOTH -> stringResource(Res.string.category_both)
            null -> stringResource(Res.string.category_archived)
        },
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@Composable
private fun CategoryRow(
    category: Category,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = categoryIcon(category.icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = if (category.isArchived) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface,
        )
        if (category.isDefault) {
            SuggestionChip(
                onClick = {},
                label = { Text(stringResource(Res.string.category_default), style = MaterialTheme.typography.labelSmall) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            )
        }
    }
}
