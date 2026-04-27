package com.github.dsbezerra.cashflow.feature.dashboard

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.github.dsbezerra.cashflow.core.designsystem.component.DesktopVerticalScrollbar
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.dsbezerra.cashflow.core.designsystem.theme.AppColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.all
import cashflow.composeapp.generated.resources.dashboard_account
import cashflow.composeapp.generated.resources.dashboard_expense_chart
import cashflow.composeapp.generated.resources.dashboard_expenses
import cashflow.composeapp.generated.resources.dashboard_income
import cashflow.composeapp.generated.resources.dashboard_income_chart
import cashflow.composeapp.generated.resources.dashboard_last_6_months
import cashflow.composeapp.generated.resources.dashboard_month_balance
import cashflow.composeapp.generated.resources.dashboard_no_data_6_months
import cashflow.composeapp.generated.resources.dashboard_no_transactions_month
import cashflow.composeapp.generated.resources.dashboard_recent
import cashflow.composeapp.generated.resources.dashboard_see_all
import cashflow.composeapp.generated.resources.transaction_count
import com.github.dsbezerra.cashflow.core.domain.model.Account
import com.github.dsbezerra.cashflow.core.domain.model.DashboardSummary
import com.github.dsbezerra.cashflow.core.domain.model.Decimal
import com.github.dsbezerra.cashflow.core.domain.model.MonthlyAmount
import com.github.dsbezerra.cashflow.core.designsystem.component.AmountText
import com.github.dsbezerra.cashflow.core.designsystem.component.TopBarWithNavigation
import com.github.dsbezerra.cashflow.util.namePtBr
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.LineProperties
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardScreen(
    onSeeAll: () -> Unit,
    onNavigateToTransaction: (String?) -> Unit,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is DashboardEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!state.isLoading) {
                TopBarWithNavigation(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = state.summary?.monthName.orEmpty(),
                                style = MaterialTheme.typography.titleMediumEmphasized,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "${state.summary?.year} · ${pluralStringResource(Res.plurals.transaction_count, state.summary?.transactionCount ?: 0, state.summary?.transactionCount ?: 0)}",
                                style = MaterialTheme.typography.titleSmallEmphasized,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onPrevious = { viewModel.onAction(DashboardAction.PreviousMonth) },
                    onNext = { viewModel.onAction(DashboardAction.NextMonth) },
                )
            }
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                DashboardContent(
                    state = state,
                    onSeeAll = onSeeAll,
                    onNavigateToTransaction = onNavigateToTransaction,
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardState,
    onSeeAll: () -> Unit,
    onNavigateToTransaction: (String?) -> Unit,
) {
    val summary = state.summary ?: return
    val listState = rememberLazyListState()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                NetBalance(summary, state)
            }
            item {
                SummaryCards(summary)
            }
            item {
                IncomeExpenseBarChart(summary.last6MonthsBreakdown)
            }
            item {
                RecentTransactions(
                    transactions = summary.recentTransactions,
                    onSeeAll = onSeeAll,
                    onTransactionClick = { onNavigateToTransaction(it) },
                )
            }
        }
        DesktopVerticalScrollbar(listState)
    }
}

@Composable
private fun AccountSelector(
    accounts: List<Account>,
    selectedAccountId: String?,
    onAccountSelected: (String?) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selectedAccountId == null,
                onClick = { onAccountSelected(null) },
                label = { Text(stringResource(Res.string.all)) },
            )
        }
        items(accounts) { account ->
            FilterChip(
                selected = account.id == selectedAccountId,
                onClick = { onAccountSelected(account.id) },
                label = { Text(account.name) },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NetBalance(summary: DashboardSummary, state: DashboardState) {
    val cashFlowColors = AppColors.colors
    val defaultAccount = remember(state.selectedAccountId) {
        state.accounts
            .find { it.isDefault && state.selectedAccountId == it.id }
            ?: state.accounts.first()
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(Res.string.dashboard_month_balance).uppercase(),
            style = MaterialTheme.typography.titleSmallEmphasized,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = summary.netBalance.toCurrency(),
            style = MaterialTheme.typography.displayLargeEmphasized,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(8.dp),
                color = cashFlowColors.income,
                shape = CircleShape,
            ) {}
            Text(
                text = stringResource(Res.string.dashboard_account, defaultAccount.name),
                style = MaterialTheme.typography.labelSmallEmphasized,
            )
        }
    }
}

@Composable
private fun SummaryCards(summary: DashboardSummary) {
    val cashFlowColors = AppColors.colors
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        SummaryCard(
            label = stringResource(Res.string.dashboard_income),
            amount = summary.monthlyIncome,
            color = cashFlowColors.income,
            icon = Icons.Filled.ArrowUpward,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            label = stringResource(Res.string.dashboard_expenses),
            amount = summary.monthlyExpenses,
            color = cashFlowColors.expense,
            icon = Icons.Filled.ArrowDownward,
            modifier = Modifier.weight(1f),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SummaryCard(
    label: String,
    amount: Decimal,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val containerColor = color
        .copy(alpha = 0.1f)
        .compositeOver(MaterialTheme.colorScheme.surface)
    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = color,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmallEmphasized,
                    fontWeight = FontWeight.SemiBold,
                )
                Surface(
                    color = color.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Icon(
                        modifier = Modifier.padding(4.dp),
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                    )
                }
            }
            Text(
                text = amount.toCurrency(),
                style = MaterialTheme.typography.titleMediumEmphasized,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun IncomeExpenseBarChart(breakdown: List<MonthlyAmount>) {
    val cashFlowColors = AppColors.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.dashboard_last_6_months),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            if (breakdown.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.dashboard_no_data_6_months),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                val onSurface = MaterialTheme.colorScheme.onSurfaceVariant
                val outlineVariant = MaterialTheme.colorScheme.outlineVariant
                val labelStyle = MaterialTheme.typography.labelSmall.copy(color = onSurface)
                ColumnChart(
                    data = breakdown.map { month ->
                        Bars(
                            label = month.month.namePtBr().take(3)
                                .replaceFirstChar { it.uppercase() },
                            values = listOf(
                                Bars.Data(
                                    label = stringResource(Res.string.dashboard_income_chart),
                                    value = month.income,
                                    color = SolidColor(cashFlowColors.income)
                                ),
                                Bars.Data(
                                    label = stringResource(Res.string.dashboard_expense_chart),
                                    value = month.expenses,
                                    color = SolidColor(cashFlowColors.expense)
                                ),
                            ),
                        )
                    },
                    barProperties = BarProperties(
                        thickness = 16.dp,
                        spacing = 4.dp,
                    ),
                    labelProperties = LabelProperties(
                        enabled = true,
                        textStyle = labelStyle,
                    ),
                    indicatorProperties = HorizontalIndicatorProperties(
                        textStyle = labelStyle,
                    ),
                    gridProperties = GridProperties(
                        xAxisProperties = GridProperties.AxisProperties(
                            color = SolidColor(
                                outlineVariant
                            )
                        ),
                        yAxisProperties = GridProperties.AxisProperties(
                            color = SolidColor(
                                outlineVariant
                            )
                        ),
                    ),
                    dividerProperties = DividerProperties(
                        xAxisProperties = LineProperties(color = SolidColor(outlineVariant)),
                        yAxisProperties = LineProperties(color = SolidColor(outlineVariant)),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RecentTransactions(
    transactions: List<RecentTransaction>,
    onSeeAll: () -> Unit,
    onTransactionClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.dashboard_recent),
                style = MaterialTheme.typography.titleSmallEmphasized,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            if (transactions.isNotEmpty()) {
                TextButton(
                    onClick = onSeeAll,
                ) {
                    Text(
                        text = stringResource(Res.string.dashboard_see_all),
                        style = MaterialTheme.typography.labelSmallEmphasized,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        if (transactions.isEmpty()) {
            Text(
                text = stringResource(Res.string.dashboard_no_transactions_month),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        } else {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Column {
                    transactions.forEach { transaction ->
                        TransactionRow(
                            transaction = transaction,
                            onTransactionClick = onTransactionClick,
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransactionRow(
    transaction: RecentTransaction,
    onTransactionClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTransactionClick(transaction.id) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyLargeEmphasized,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${transaction.category} - ${transaction.dateFormatted}",
                style = MaterialTheme.typography.bodySmallEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        AmountText(amount = transaction.amount, type = transaction.type)
    }
}
