package com.github.dsbezerra.cashflow.feature.transaction.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.github.dsbezerra.cashflow.core.designsystem.component.DesktopVerticalScrollbar
import com.github.dsbezerra.cashflow.core.designsystem.component.GroupedItemPosition
import com.github.dsbezerra.cashflow.core.designsystem.component.GroupedListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.expense
import cashflow.composeapp.generated.resources.income
import cashflow.composeapp.generated.resources.transaction_empty_subtitle
import cashflow.composeapp.generated.resources.transaction_empty_title
import cashflow.composeapp.generated.resources.transaction_filter_all
import cashflow.composeapp.generated.resources.transaction_search_hint
import cashflow.composeapp.generated.resources.transaction_transfer
import com.github.dsbezerra.cashflow.core.designsystem.component.AmountText
import com.github.dsbezerra.cashflow.core.designsystem.component.DSFullscreenLoader
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
    val typeFilter by viewModel.typeFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is TransactionListEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            FilterRow(
                selectedType = typeFilter,
                searchQuery = searchQuery,
                onTypeSelected = { viewModel.onAction(TransactionListAction.TypeFilterChanged(it)) },
                onSearchQueryChanged = {
                    viewModel.onAction(
                        TransactionListAction.SearchQueryChanged(
                            it
                        )
                    )
                },
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
}

@Composable
private fun FilterRow(
    selectedType: TransactionType?,
    searchQuery: String,
    onTypeSelected: (TransactionType?) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = selectedType == null,
                onClick = { onTypeSelected(null) },
                label = { Text(stringResource(Res.string.transaction_filter_all)) },
            )
            FilterChip(
                selected = selectedType == TransactionType.INCOME,
                onClick = { onTypeSelected(if (selectedType == TransactionType.INCOME) null else TransactionType.INCOME) },
                label = { Text(stringResource(Res.string.income)) },
            )
            FilterChip(
                selected = selectedType == TransactionType.EXPENSE,
                onClick = { onTypeSelected(if (selectedType == TransactionType.EXPENSE) null else TransactionType.EXPENSE) },
                label = { Text(stringResource(Res.string.expense)) },
            )
            FilterChip(
                selected = selectedType == TransactionType.TRANSFER,
                onClick = { onTypeSelected(if (selectedType == TransactionType.TRANSFER) null else TransactionType.TRANSFER) },
                label = { Text(stringResource(Res.string.transaction_transfer)) },
            )
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(Res.string.transaction_search_hint)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
        )
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
