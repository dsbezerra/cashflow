package com.github.dsbezerra.cashflow.feature.account.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.github.dsbezerra.cashflow.core.designsystem.component.DesktopVerticalScrollbar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import com.github.dsbezerra.cashflow.core.designsystem.component.IconButtonWithTooltip
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
import androidx.compose.ui.unit.dp
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.account_default_tooltip
import cashflow.composeapp.generated.resources.account_empty_subtitle
import cashflow.composeapp.generated.resources.account_empty_title
import cashflow.composeapp.generated.resources.account_set_default
import com.github.dsbezerra.cashflow.core.designsystem.component.accountIcon
import com.github.dsbezerra.cashflow.core.designsystem.component.DSFullscreenLoader
import com.github.dsbezerra.cashflow.feature.account.accountTypeName
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountListScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: AccountListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AccountListEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                DSFullscreenLoader()
            } else if (state.accounts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(Res.string.account_empty_title), style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(Res.string.account_empty_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(state.accounts, key = { it.account.id }) { item ->
                        AccountRow(
                            item = item,
                            onClick = { onNavigateToDetail(item.account.id) },
                            onSetDefault = { viewModel.onAction(AccountListAction.SetDefault(item.account.id)) },
                        )
                        HorizontalDivider()
                    }
                }
                DesktopVerticalScrollbar(listState)
            }
        }
    }
}

@Composable
private fun AccountRow(
    item: AccountWithBalance,
    onClick: () -> Unit,
    onSetDefault: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = accountIcon(item.account.icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Column(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
            Text(item.account.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                accountTypeName(item.account.type),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = item.balance.toCurrency(),
            style = MaterialTheme.typography.bodyLarge,
            color = if (item.balance.toDouble() >= 0) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
        )
        IconButtonWithTooltip(
            onClick = onSetDefault,
            tooltip = if (item.account.isDefault) stringResource(Res.string.account_default_tooltip) else stringResource(Res.string.account_set_default),
        ) {
            Icon(
                imageVector = if (item.account.isDefault) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = if (item.account.isDefault) stringResource(Res.string.account_default_tooltip) else stringResource(Res.string.account_set_default),
                tint = if (item.account.isDefault) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
