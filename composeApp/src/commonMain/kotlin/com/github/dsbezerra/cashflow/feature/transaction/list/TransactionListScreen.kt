package com.github.dsbezerra.cashflow.feature.transaction.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.github.dsbezerra.cashflow.core.designsystem.component.DesktopVerticalScrollbar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.dsbezerra.cashflow.core.domain.model.Transaction
import com.github.dsbezerra.cashflow.core.designsystem.component.AmountText
import com.github.dsbezerra.cashflow.core.designsystem.component.DSFullscreenLoader
import com.github.dsbezerra.cashflow.feature.dashboard.RecentTransaction
import com.github.dsbezerra.cashflow.util.formatFullPtBr
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransactionListScreen(
    onNavigateToDetail: (String?) -> Unit,
    viewModel: TransactionListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                DSFullscreenLoader()
            } else if (state.transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nenhuma transação ainda", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Toque em + para adicionar sua primeira transação",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                val grouped = state.transactions.groupBy { tx ->
                    Instant.fromEpochMilliseconds(tx.date)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                }
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    grouped.forEach { (date, transactions) ->
                        stickyHeader(key = date.toString()) {
                            DateHeader(
                                date
                            )
                        }
                        items(transactions, key = { it.id }) { tx ->
                            TransactionRow(
                                transaction = tx,
                                onClick = { onNavigateToDetail(tx.id) },
                            )
                            HorizontalDivider()
                        }
                    }
                }
                DesktopVerticalScrollbar(listState)
            }
        }
    }
}

@Composable
private fun DateHeader(date: LocalDate) {
    Text(
        text = date.formatFullPtBr(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
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
        Text(
            text = transaction.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        AmountText(amount = transaction.amount.toCurrency(), type = transaction.type)
    }
}
