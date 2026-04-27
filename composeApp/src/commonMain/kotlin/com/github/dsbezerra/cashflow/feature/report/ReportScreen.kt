package com.github.dsbezerra.cashflow.feature.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.ui.text.style.TextOverflow
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.all
import cashflow.composeapp.generated.resources.report_accumulated_balance
import cashflow.composeapp.generated.resources.report_avg_daily_expenses
import cashflow.composeapp.generated.resources.report_balance
import cashflow.composeapp.generated.resources.report_balance_evolution
import cashflow.composeapp.generated.resources.report_by_category
import cashflow.composeapp.generated.resources.report_by_period
import cashflow.composeapp.generated.resources.report_costs
import cashflow.composeapp.generated.resources.report_deductions
import cashflow.composeapp.generated.resources.report_dre
import cashflow.composeapp.generated.resources.report_dre_error
import cashflow.composeapp.generated.resources.report_dre_no_movements
import cashflow.composeapp.generated.resources.report_expenses
import cashflow.composeapp.generated.resources.report_expenses_by_category
import cashflow.composeapp.generated.resources.report_gross_profit
import cashflow.composeapp.generated.resources.report_gross_revenue
import cashflow.composeapp.generated.resources.report_gross_revenue_sign
import cashflow.composeapp.generated.resources.report_income
import cashflow.composeapp.generated.resources.report_income_by_category
import cashflow.composeapp.generated.resources.report_month_to_month
import cashflow.composeapp.generated.resources.report_most_used_category
import cashflow.composeapp.generated.resources.report_net_revenue
import cashflow.composeapp.generated.resources.report_next_month
import cashflow.composeapp.generated.resources.report_no_transactions_category
import cashflow.composeapp.generated.resources.report_no_transactions_period
import cashflow.composeapp.generated.resources.report_operational_expenses
import cashflow.composeapp.generated.resources.report_operational_expenses_sign
import cashflow.composeapp.generated.resources.report_period_last_3_months
import cashflow.composeapp.generated.resources.report_period_last_6_months
import cashflow.composeapp.generated.resources.report_period_last_month
import cashflow.composeapp.generated.resources.report_period_this_month
import cashflow.composeapp.generated.resources.report_period_this_year
import cashflow.composeapp.generated.resources.report_prev_month
import cashflow.composeapp.generated.resources.report_profit_loss
import cashflow.composeapp.generated.resources.report_top_expense_category
import cashflow.composeapp.generated.resources.transaction_count
import com.github.dsbezerra.cashflow.core.designsystem.component.IconButtonWithTooltip
import com.github.dsbezerra.cashflow.core.domain.model.Account
import com.github.dsbezerra.cashflow.core.domain.model.CategoryAmount
import com.github.dsbezerra.cashflow.core.domain.model.DailyAmount
import com.github.dsbezerra.cashflow.core.domain.model.DreLineItem
import com.github.dsbezerra.cashflow.core.domain.model.DreReport
import com.github.dsbezerra.cashflow.core.domain.model.MonthlyAmount
import com.github.dsbezerra.cashflow.core.domain.model.ReportData
import com.github.dsbezerra.cashflow.core.domain.model.ReportPeriod
import com.github.dsbezerra.cashflow.core.domain.model.toDecimal
import com.github.dsbezerra.cashflow.core.designsystem.component.DesktopVerticalScrollbar
import com.github.dsbezerra.cashflow.util.namePtBr
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import com.github.dsbezerra.cashflow.core.designsystem.theme.AppColors
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReportScreen(viewModel: ReportViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ReportEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                ReportContent(
                    state = state,
                    onAction = viewModel::onAction
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReportContent(state: ReportState, onAction: (ReportAction) -> Unit) {
    val data = state.data ?: return
    val cashFlowColors = AppColors.colors
    val listState = rememberLazyListState()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                // Period selector
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max)) {
                    ReportPeriod.entries.forEachIndexed { index, period ->
                        SegmentedButton(
                            modifier = Modifier.fillMaxHeight(),
                            selected = state.selectedPeriod == period,
                            onClick = { onAction(ReportAction.PeriodChanged(period)) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = ReportPeriod.entries.size,
                            ),
                            label = {
                                Text(
                                    text = stringResource(period.res),
                                    style = MaterialTheme.typography.labelSmallEmphasized,
                                )
                            },
                        )
                    }
                }
            }

            item {
                TabRow(selectedTabIndex = state.selectedTab.ordinal) {
                    Tab(
                        selected = state.selectedTab == ReportTab.BY_PERIOD,
                        onClick = {
                            onAction(
                                ReportAction.TabChanged(
                                    ReportTab.BY_PERIOD
                                )
                            )
                        },
                        text = { Text(stringResource(Res.string.report_by_period)) },
                    )
                    Tab(
                        selected = state.selectedTab == ReportTab.BY_CATEGORY,
                        onClick = {
                            onAction(
                                ReportAction.TabChanged(
                                    ReportTab.BY_CATEGORY
                                )
                            )
                        },
                        text = { Text(stringResource(Res.string.report_by_category)) },
                    )
                    Tab(
                        selected = state.selectedTab == ReportTab.DRE,
                        onClick = {
                            onAction(
                                ReportAction.TabChanged(
                                    ReportTab.DRE
                                )
                            )
                        },
                        text = { Text(stringResource(Res.string.report_dre)) },
                    )
                }
            }

            if (state.selectedTab == ReportTab.BY_PERIOD) {
                item {
                    PeriodSummaryCards(
                        data
                    )
                }
                if (data.totalIncome == 0.0 && data.totalExpenses == 0.0) {
                    item {
                        EmptyState(
                            stringResource(Res.string.report_no_transactions_period)
                        )
                    }
                } else {
                    item {
                        PeriodExtraStats(
                            data
                        )
                    }
                    if (data.dailyCumulative.isNotEmpty()) {
                        item {
                            DailyCumulativeLineChart(
                                data.dailyCumulative
                            )
                        }
                    }
                    if (data.monthlyBreakdown.isNotEmpty()) {
                        item {
                            MonthlyBarChart(
                                data.monthlyBreakdown
                            )
                        }
                    }
                }
            } else if (state.selectedTab == ReportTab.BY_CATEGORY) {
                item {
                    CategoryBreakdownSection(
                        title = stringResource(Res.string.report_expenses_by_category),
                        items = data.expenseByCategory,
                        color = cashFlowColors.expense,
                    )
                }
                if (data.incomeByCategory.isNotEmpty()) {
                    item {
                        CategoryBreakdownSection(
                            title = stringResource(Res.string.report_income_by_category),
                            items = data.incomeByCategory,
                            color = cashFlowColors.income,
                        )
                    }
                }
                if (data.expenseByCategory.isEmpty() && data.incomeByCategory.isEmpty()) {
                    item {
                        EmptyState(
                            stringResource(Res.string.report_no_transactions_period)
                        )
                    }
                }
            } else {
                // DRE tab
                item {
                    DreMonthSelector(
                        year = state.dreYear,
                        month = state.dreMonth,
                        onAction = onAction,
                    )
                }
                if (state.isDreLoading) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    val dre = state.dreReport
                    if (dre == null) {
                        item {
                            EmptyState(
                                stringResource(Res.string.report_dre_error)
                            )
                        }
                    } else if (dre.grossRevenue.total == 0.0 &&
                        dre.deductions.total == 0.0 &&
                        dre.costs.total == 0.0 &&
                        dre.operationalExpenses.total == 0.0
                    ) {
                        item {
                            EmptyState(
                                stringResource(
                                    Res.string.report_dre_no_movements,
                                    Month(state.dreMonth).namePtBr()
                                        .replaceFirstChar { it.uppercase() },
                                    state.dreYear,
                                )
                            )
                        }
                    } else {
                        item {
                            DreStatement(
                                dre
                            )
                        }
                    }
                }
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

@Composable
private fun PeriodSummaryCards(data: ReportData) {
    val cashFlowColors = AppColors.colors
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        SummaryCard(
            label = stringResource(Res.string.report_income),
            amount = data.totalIncome,
            color = cashFlowColors.income,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            label = stringResource(Res.string.report_expenses),
            amount = data.totalExpenses,
            color = cashFlowColors.expense,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            label = stringResource(Res.string.report_balance),
            amount = data.netBalance,
            color = if (data.netBalance >= 0) cashFlowColors.income else cashFlowColors.expense,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SummaryCard(
    label: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = amount.toDecimal().toCurrency(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }
}

@Composable
private fun PeriodExtraStats(data: ReportData) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatRow(
                label = stringResource(Res.string.report_avg_daily_expenses),
                value = data.averageDailyExpense.toDecimal().toCurrency(),
            )
            if (data.highestExpenseCategory != null) {
                StatRow(
                    label = stringResource(Res.string.report_top_expense_category),
                    value = data.highestExpenseCategory.name,
                )
            }
            if (data.mostUsedCategory != null) {
                StatRow(
                    label = stringResource(Res.string.report_most_used_category),
                    value = data.mostUsedCategory.name,
                )
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DailyCumulativeLineChart(data: List<DailyAmount>) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = onSurface)
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.report_balance_evolution),
            style = MaterialTheme.typography.titleMediumEmphasized,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = MaterialTheme.shapes.small
        ) {
            LineChart(
                data = listOf(
                    Line(
                        label = stringResource(Res.string.report_accumulated_balance),
                        values = data.map { it.cumulativeNet },
                        color = SolidColor(MaterialTheme.colorScheme.primary),
                    ),
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
                    .height(200.dp)
                    .padding(16.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MonthlyBarChart(data: List<MonthlyAmount>) {
    val cashFlowColors = AppColors.colors
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.report_month_to_month),
            style = MaterialTheme.typography.titleMediumEmphasized,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        val onSurface = MaterialTheme.colorScheme.onSurface
        val outlineVariant = MaterialTheme.colorScheme.outlineVariant
        val labelStyle = MaterialTheme.typography.labelSmall.copy(color = onSurface)
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = MaterialTheme.shapes.small
        ) {
            ColumnChart(
                data = data.map { month ->
                    Bars(
                        label = month.month.namePtBr().take(3).replaceFirstChar { it.uppercase() },
                        values = listOf(
                            Bars.Data(
                                value = month.income,
                                color = SolidColor(cashFlowColors.income)
                            ),
                            Bars.Data(
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
                    .height(200.dp)
                    .padding(16.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CategoryBreakdownSection(
    title: String,
    items: List<CategoryAmount>,
    color: Color,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMediumEmphasized,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        CategoryBreakdownList(
            items = items,
            color = color
        )
    }
}

@Composable
private fun CategoryBreakdownList(items: List<CategoryAmount>, color: Color) {
    if (items.isEmpty()) {
        EmptyState(stringResource(Res.string.report_no_transactions_category))
        return
    }

    val maxAmount = items.maxOf { it.amount }
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.small
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEachIndexed { index, item ->
                CategoryAmountRow(
                    item = item,
                    maxAmount = maxAmount,
                    color = color
                )
                if (index != items.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surface,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryAmountRow(
    item: CategoryAmount,
    maxAmount: Double,
    color: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color, CircleShape),
                )
                Text(
                    text = item.category.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = item.amount.toDecimal().toCurrency(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                )
                Text(
                    text = pluralStringResource(
                        Res.plurals.transaction_count,
                        item.count,
                        item.count
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        LinearProgressIndicator(
            progress = { (item.amount / maxAmount).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            color = color,
            trackColor = color.copy(alpha = 0.15f),
        )
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DreMonthSelector(year: Int, month: Int, onAction: (ReportAction) -> Unit) {
    val monthName = Month(month).namePtBr()
        .replaceFirstChar { it.uppercase() }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        IconButtonWithTooltip(
            onClick = {
                val prev = if (month == 1) Pair(year - 1, 12) else Pair(year, month - 1)
                onAction(ReportAction.DreMonthChanged(prev.first, prev.second))
            },
            tooltip = stringResource(Res.string.report_prev_month),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.report_prev_month)
            )
        }
        Text(
            text = "$monthName $year",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        IconButtonWithTooltip(
            onClick = {
                val next = if (month == 12) Pair(year + 1, 1) else Pair(year, month + 1)
                onAction(ReportAction.DreMonthChanged(next.first, next.second))
            },
            tooltip = stringResource(Res.string.report_next_month),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(Res.string.report_next_month)
            )
        }
    }
}

@Composable
private fun DreStatement(dre: DreReport) {
    val cashFlowColors = AppColors.colors
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            DreSection(
                sign = stringResource(Res.string.report_gross_revenue_sign),
                label = stringResource(Res.string.report_gross_revenue),
                item = dre.grossRevenue,
                color = cashFlowColors.income,
            )
            DreResultRow(
                label = stringResource(Res.string.report_deductions),
                item = dre.deductions,
                color = cashFlowColors.expense
            )
            DreTotalRow(
                label = stringResource(Res.string.report_net_revenue),
                amount = dre.netRevenue
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.surface)
            DreResultRow(
                label = stringResource(Res.string.report_costs),
                item = dre.costs,
                color = cashFlowColors.expense
            )
            DreTotalRow(
                label = stringResource(Res.string.report_gross_profit),
                amount = dre.grossProfit
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.surface)
            DreSection(
                sign = stringResource(Res.string.report_operational_expenses_sign),
                label = stringResource(Res.string.report_operational_expenses),
                item = dre.operationalExpenses,
                color = cashFlowColors.expense,
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.surface)
            DreTotalRow(
                label = stringResource(Res.string.report_profit_loss),
                amount = dre.netResult,
                highlight = true,
            )
        }
    }
}

@Composable
private fun DreSection(sign: String, label: String, item: DreLineItem, color: Color) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$sign $label",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = item.total.toDecimal().toCurrency(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = color,
            )
        }
        if (expanded) {
            item.categories.forEach { line ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = line.category.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = line.amount.toDecimal().toCurrency(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun DreResultRow(label: String, item: DreLineItem, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = item.total.toDecimal().toCurrency(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
        )
    }
}

@Composable
private fun DreTotalRow(label: String, amount: Double, highlight: Boolean = false) {
    val cashFlowColors = AppColors.colors
    val color = if (amount >= 0) cashFlowColors.income else cashFlowColors.expense
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = if (highlight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = amount.toDecimal().toCurrency(),
            style = if (highlight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}
