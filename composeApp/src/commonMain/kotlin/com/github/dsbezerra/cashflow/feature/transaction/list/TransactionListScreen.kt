package com.github.dsbezerra.cashflow.feature.transaction.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.github.dsbezerra.cashflow.core.designsystem.component.ChipSelector
import com.github.dsbezerra.cashflow.core.designsystem.component.DesktopVerticalScrollbar
import com.github.dsbezerra.cashflow.core.designsystem.component.GroupedItemPosition
import com.github.dsbezerra.cashflow.core.designsystem.component.GroupedListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.expense
import cashflow.composeapp.generated.resources.income
import cashflow.composeapp.generated.resources.nav_transactions
import cashflow.composeapp.generated.resources.transaction_empty_subtitle
import cashflow.composeapp.generated.resources.transaction_empty_title
import cashflow.composeapp.generated.resources.transaction_filter_all
import cashflow.composeapp.generated.resources.transaction_filter_all_categories
import cashflow.composeapp.generated.resources.transaction_filter_apply
import cashflow.composeapp.generated.resources.transaction_filter_button
import cashflow.composeapp.generated.resources.transaction_filter_category
import cashflow.composeapp.generated.resources.transaction_filter_clear
import cashflow.composeapp.generated.resources.transaction_filter_last_month
import cashflow.composeapp.generated.resources.transaction_filter_period
import cashflow.composeapp.generated.resources.transaction_filter_this_month
import cashflow.composeapp.generated.resources.transaction_filter_this_week
import cashflow.composeapp.generated.resources.transaction_filter_this_year
import cashflow.composeapp.generated.resources.transaction_filter_type
import cashflow.composeapp.generated.resources.transaction_search_hint
import cashflow.composeapp.generated.resources.transaction_transfer
import com.github.dsbezerra.cashflow.core.designsystem.component.AmountText
import com.github.dsbezerra.cashflow.core.designsystem.component.DSFullscreenLoader
import com.github.dsbezerra.cashflow.core.domain.model.Category
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
import com.github.dsbezerra.cashflow.util.formatFullPtBr
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TransactionListScreen(
    onNavigateToDetail: (String?) -> Unit,
    viewModel: TransactionListViewModel = koinViewModel(),
) {
    val lazyPagingItems: LazyPagingItems<TransactionListItem> =
        viewModel.transactions.collectAsLazyPagingItems()
    val state by viewModel.state.collectAsState()
    val typeFilter by viewModel.typeFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categoryFilter by viewModel.categoryFilter.collectAsState()
    val periodFilter by viewModel.periodFilter.collectAsState()
    val activeFilterCount by viewModel.activeFilterCount.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    var showFilterSheet by rememberSaveable { mutableStateOf(false) }
    var draftType by remember { mutableStateOf<TransactionType?>(null) }
    var draftPeriod by remember { mutableStateOf<TransactionListPeriod?>(null) }
    var draftCategoryId by remember { mutableStateOf<String?>(null) }

    // Sync draft from applied filters whenever the sheet opens
    LaunchedEffect(showFilterSheet) {
        if (showFilterSheet) {
            draftType = typeFilter
            draftPeriod = periodFilter
            draftCategoryId = categoryFilter
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is TransactionListEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.nav_transactions)) },
                actions = {
                    BadgedBox(
                        badge = {
                            if (activeFilterCount > 0) Badge { Text("$activeFilterCount") }
                        }
                    ) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = stringResource(Res.string.transaction_filter_button),
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onAction(TransactionListAction.SearchQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                placeholder = { Text(stringResource(Res.string.transaction_search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
            )
            val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
                    && lazyPagingItems.itemCount > 0
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { lazyPagingItems.refresh() },
                modifier = Modifier.fillMaxSize(),
            ) {
                when {
                    lazyPagingItems.loadState.refresh is LoadState.Loading && lazyPagingItems.itemCount == 0 -> {
                        DSFullscreenLoader()
                    }

                    lazyPagingItems.itemCount == 0 -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    stringResource(Res.string.transaction_empty_title),
                                    style = MaterialTheme.typography.titleMediumEmphasized,
                                )
                                Text(
                                    stringResource(Res.string.transaction_empty_subtitle),
                                    style = MaterialTheme.typography.bodyMediumEmphasized,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(
                                count = lazyPagingItems.itemCount,
                                key = lazyPagingItems.itemKey { item ->
                                    when (item) {
                                        is TransactionListItem.Header -> "header-${item.date}"
                                        is TransactionListItem.Entry -> item.transaction.id
                                    }
                                },
                            ) { index ->
                                when (val item = lazyPagingItems[index]) {
                                    is TransactionListItem.Header -> DateHeader(item.date)
                                    is TransactionListItem.Entry -> {
                                        val prevIsHeader =
                                            index == 0 || lazyPagingItems[index - 1] is TransactionListItem.Header
                                        val nextIsHeader = index == lazyPagingItems.itemCount - 1
                                                || lazyPagingItems[index + 1] is TransactionListItem.Header
                                                || lazyPagingItems[index + 1] == null
                                        val position = when {
                                            prevIsHeader && nextIsHeader -> GroupedItemPosition.Single
                                            prevIsHeader -> GroupedItemPosition.Top
                                            nextIsHeader -> GroupedItemPosition.Bottom
                                            else -> GroupedItemPosition.Middle
                                        }
                                        GroupedListItem(position = position) {
                                            TransactionRow(
                                                transaction = item.transaction,
                                                onClick = { onNavigateToDetail(item.transaction.id) },
                                            )
                                        }
                                    }

                                    null -> {}
                                }
                            }

                            if (lazyPagingItems.loadState.append is LoadState.Loading) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                        DesktopVerticalScrollbar(listState)
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            draftType = draftType,
            draftPeriod = draftPeriod,
            draftCategoryId = draftCategoryId,
            categories = state.categories,
            onDraftTypeChanged = { draftType = it },
            onDraftPeriodChanged = { draftPeriod = it },
            onDraftCategoryChanged = { draftCategoryId = it },
            onApply = {
                viewModel.onAction(
                    TransactionListAction.ApplyFilters(draftType, draftPeriod, draftCategoryId)
                )
                showFilterSheet = false
            },
            onClear = {
                draftType = null
                draftPeriod = null
                draftCategoryId = null
                viewModel.onAction(TransactionListAction.ApplyFilters(null, null, null))
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    draftType: TransactionType?,
    draftPeriod: TransactionListPeriod?,
    draftCategoryId: String?,
    categories: List<Category>,
    onDraftTypeChanged: (TransactionType?) -> Unit,
    onDraftPeriodChanged: (TransactionListPeriod?) -> Unit,
    onDraftCategoryChanged: (String?) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Type section
            ChipSelector(
                label = stringResource(Res.string.transaction_filter_type),
                selectedId = draftType?.name ?: "",
                items = listOf(
                    "" to stringResource(Res.string.transaction_filter_all),
                    TransactionType.INCOME.name to stringResource(Res.string.income),
                    TransactionType.EXPENSE.name to stringResource(Res.string.expense),
                    TransactionType.TRANSFER.name to stringResource(Res.string.transaction_transfer),
                ),
                onSelect = { id ->
                    onDraftTypeChanged(if (id.isEmpty()) null else TransactionType.valueOf(id))
                },
            )

            // Period section
            ChipSelector(
                label = stringResource(Res.string.transaction_filter_period),
                selectedId = draftPeriod?.name ?: "",
                items = listOf(
                    "" to stringResource(Res.string.transaction_filter_all),
                    TransactionListPeriod.THIS_WEEK.name to stringResource(Res.string.transaction_filter_this_week),
                    TransactionListPeriod.THIS_MONTH.name to stringResource(Res.string.transaction_filter_this_month),
                    TransactionListPeriod.LAST_MONTH.name to stringResource(Res.string.transaction_filter_last_month),
                    TransactionListPeriod.THIS_YEAR.name to stringResource(Res.string.transaction_filter_this_year),
                ),
                onSelect = { id ->
                    onDraftPeriodChanged(
                        if (id.isEmpty()) null else TransactionListPeriod.valueOf(id)
                    )
                },
            )

            // Category section
            if (categories.isNotEmpty()) {
                val categoryItems = listOf("" to stringResource(Res.string.transaction_filter_all_categories)) +
                        categories.map { it.id to it.name }
                ChipSelector(
                    label = stringResource(Res.string.transaction_filter_category),
                    selectedId = draftCategoryId ?: "",
                    items = categoryItems,
                    onSelect = { id ->
                        onDraftCategoryChanged(id.ifEmpty { null })
                    },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onClear) {
                    Text(stringResource(Res.string.transaction_filter_clear))
                }
                Button(onClick = onApply) {
                    Text(stringResource(Res.string.transaction_filter_apply))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DateHeader(date: LocalDate) {
    Text(
        text = date.formatFullPtBr(),
        style = MaterialTheme.typography.titleLargeEmphasized,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransactionRow(
    transaction: TransactionUiModel,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyLargeEmphasized,
            )
            Text(
                text = transaction.categoryName,
                style = MaterialTheme.typography.bodySmallEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        AmountText(amount = transaction.amount.toCurrency(), type = transaction.type)
    }
}
