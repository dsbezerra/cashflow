package com.github.dsbezerra.cashflow.feature.recurring.list

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import com.github.dsbezerra.cashflow.core.designsystem.component.IconButtonWithTooltip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.back
import cashflow.composeapp.generated.resources.days
import cashflow.composeapp.generated.resources.months
import cashflow.composeapp.generated.resources.recurring_active
import cashflow.composeapp.generated.resources.recurring_add
import cashflow.composeapp.generated.resources.recurring_empty_subtitle
import cashflow.composeapp.generated.resources.recurring_empty_title
import cashflow.composeapp.generated.resources.recurring_freq_daily
import cashflow.composeapp.generated.resources.recurring_freq_monthly
import cashflow.composeapp.generated.resources.recurring_freq_weekly
import cashflow.composeapp.generated.resources.recurring_freq_yearly
import cashflow.composeapp.generated.resources.recurring_frequency_format
import cashflow.composeapp.generated.resources.recurring_next_date
import cashflow.composeapp.generated.resources.recurring_paused
import cashflow.composeapp.generated.resources.recurring_title
import cashflow.composeapp.generated.resources.weeks
import cashflow.composeapp.generated.resources.years
import com.github.dsbezerra.cashflow.core.domain.model.Frequency
import com.github.dsbezerra.cashflow.core.domain.model.RecurringRule
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
import com.github.dsbezerra.cashflow.core.designsystem.component.DesktopVerticalScrollbar
import com.github.dsbezerra.cashflow.core.designsystem.component.GroupedListItem
import com.github.dsbezerra.cashflow.core.designsystem.component.groupedItems
import com.github.dsbezerra.cashflow.util.formatFullPtBr
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.github.dsbezerra.cashflow.core.designsystem.theme.AppColors
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringRuleListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToForm: (String?) -> Unit,
    viewModel: RecurringRuleListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is RecurringRuleListEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.recurring_title)) },
                navigationIcon = {
                    IconButtonWithTooltip(onClick = onNavigateBack, tooltip = stringResource(Res.string.back)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToForm(null) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.recurring_add))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.rules.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            stringResource(Res.string.recurring_empty_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            stringResource(Res.string.recurring_empty_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                else -> {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                        groupedItems(state.rules, key = { it.id }) { rule, position ->
                            GroupedListItem(position = position) {
                                RecurringRuleRow(
                                    rule = rule,
                                    onClick = { onNavigateToForm(rule.id) },
                                    onToggleActive = { isActive ->
                                        viewModel.onAction(
                                            RecurringRuleListAction.ToggleActive(
                                                rule.id,
                                                isActive
                                            )
                                        )
                                    },
                                )
                            }
                        }
                    }
                    DesktopVerticalScrollbar(listState)
                }
            }
        }
    }
}

@Composable
private fun RecurringRuleRow(
    rule: RecurringRule,
    onClick: () -> Unit,
    onToggleActive: (Boolean) -> Unit,
) {
    val cashFlowColors = AppColors.colors
    val tz = TimeZone.currentSystemDefault()
    val nextDate = Instant.fromEpochMilliseconds(rule.nextOccurrence)
        .toLocalDateTime(tz).date
    val amountColor = when (rule.type) {
        TransactionType.INCOME -> cashFlowColors.income
        TransactionType.EXPENSE -> cashFlowColors.expense
        TransactionType.TRANSFER -> Color.Unspecified
    }
    val freqLabel = when (rule.frequency) {
        Frequency.DAILY -> stringResource(Res.string.recurring_freq_daily)
        Frequency.WEEKLY -> stringResource(Res.string.recurring_freq_weekly)
        Frequency.MONTHLY -> stringResource(Res.string.recurring_freq_monthly)
        Frequency.YEARLY -> stringResource(Res.string.recurring_freq_yearly)
    }
    val unitLabel = when (rule.frequency) {
        Frequency.DAILY -> pluralStringResource(Res.plurals.days, rule.interval)
        Frequency.WEEKLY -> pluralStringResource(Res.plurals.weeks, rule.interval)
        Frequency.MONTHLY -> pluralStringResource(Res.plurals.months, rule.interval)
        Frequency.YEARLY -> pluralStringResource(Res.plurals.years, rule.interval)
    }
    val frequencyLabel = stringResource(Res.string.recurring_frequency_format, freqLabel, rule.interval, unitLabel)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = rule.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = rule.amount.toCurrency(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = amountColor,
                )
            }
            Text(
                text = frequencyLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(Res.string.recurring_next_date, nextDate.formatFullPtBr()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        FilterChip(
            selected = rule.isActive,
            onClick = { onToggleActive(!rule.isActive) },
            label = { Text(if (rule.isActive) stringResource(Res.string.recurring_active) else stringResource(Res.string.recurring_paused)) },
            modifier = Modifier.padding(start = 8.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = cashFlowColors.income.copy(alpha = 0.2f),
                selectedLabelColor = cashFlowColors.income,
            ),
        )
    }
}

