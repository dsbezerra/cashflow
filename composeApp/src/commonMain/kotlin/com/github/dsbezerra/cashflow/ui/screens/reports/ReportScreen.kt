package com.github.dsbezerra.cashflow.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.github.dsbezerra.cashflow.domain.model.Account
import com.github.dsbezerra.cashflow.domain.model.CategoryAmount
import com.github.dsbezerra.cashflow.domain.model.DailyAmount
import com.github.dsbezerra.cashflow.domain.model.DreLineItem
import com.github.dsbezerra.cashflow.domain.model.DreReport
import com.github.dsbezerra.cashflow.domain.model.MonthlyAmount
import com.github.dsbezerra.cashflow.domain.model.ReportData
import com.github.dsbezerra.cashflow.domain.model.ReportPeriod
import com.github.dsbezerra.cashflow.ui.common.DesktopVerticalScrollbar
import com.github.dsbezerra.cashflow.util.namePtBr
import kotlinx.datetime.Month
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
import com.github.dsbezerra.cashflow.ui.theme.AppColors
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

@OptIn(ExperimentalMaterial3Api::class)
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
                AccountSelector(
                    accounts = state.accounts,
                    selectedAccountId = state.selectedAccountId,
                    onAccountSelected = { onAction(ReportAction.AccountSelected(it)) },
                )
            }
            item {
                // Period selector
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ReportPeriod.entries.forEachIndexed { index, period ->
                        SegmentedButton(
                            selected = state.selectedPeriod == period,
                            onClick = { onAction(ReportAction.PeriodChanged(period)) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = ReportPeriod.entries.size,
                            ),
                            label = {
                                Text(
                                    text = period.labelPtBr,
                                    style = MaterialTheme.typography.labelSmall,
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
                        onClick = { onAction(
                            ReportAction.TabChanged(
                                ReportTab.BY_PERIOD)) },
                        text = { Text("Por Período") },
                    )
                    Tab(
                        selected = state.selectedTab == ReportTab.BY_CATEGORY,
                        onClick = { onAction(
                            ReportAction.TabChanged(
                                ReportTab.BY_CATEGORY)) },
                        text = { Text("Por Categoria") },
                    )
                    Tab(
                        selected = state.selectedTab == ReportTab.DRE,
                        onClick = { onAction(
                            ReportAction.TabChanged(
                                ReportTab.DRE)) },
                        text = { Text("DRE") },
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
                            "Nenhuma transação no período selecionado"
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
                        title = "Despesas por categoria",
                        items = data.expenseByCategory,
                        color = cashFlowColors.expense,
                    )
                }
                if (data.incomeByCategory.isNotEmpty()) {
                    item {
                        CategoryBreakdownSection(
                            title = "Receitas por categoria",
                            items = data.incomeByCategory,
                            color = cashFlowColors.income,
                        )
                    }
                }
                if (data.expenseByCategory.isEmpty() && data.incomeByCategory.isEmpty()) {
                    item {
                        EmptyState(
                            "Nenhuma transação no período selecionado"
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
                        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    val dre = state.dreReport
                    if (dre == null) {
                        item {
                            EmptyState(
                                "Erro ao carregar DRE"
                            )
                        }
                    } else if (dre.grossRevenue.total == 0.0 &&
                        dre.deductions.total == 0.0 &&
                        dre.costs.total == 0.0 &&
                        dre.operationalExpenses.total == 0.0
                    ) {
                        item {
                            EmptyState(
                                "Sem movimentações em " +
                                        Month(state.dreMonth).namePtBr()
                                            .replaceFirstChar { it.uppercase() } +
                                        " ${state.dreYear}"
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
                label = { Text("Todas") },
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
            label = "Receita",
            amount = data.totalIncome,
            color = cashFlowColors.income,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            label = "Despesas",
            amount = data.totalExpenses,
            color = cashFlowColors.expense,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            label = "Saldo",
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
                text = "${"%.2f".format(amount)}",
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
                label = "Média diária de despesas",
                value = "${"%.2f".format(data.averageDailyExpense)}",
            )
            if (data.highestExpenseCategory != null) {
                StatRow(
                    label = "Maior categoria de despesa",
                    value = data.highestExpenseCategory.name,
                )
            }
            if (data.mostUsedCategory != null) {
                StatRow(
                    label = "Categoria mais usada",
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

@Composable
private fun DailyCumulativeLineChart(data: List<DailyAmount>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Evolução do saldo",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        val onSurface = MaterialTheme.colorScheme.onSurface
        val outlineVariant = MaterialTheme.colorScheme.outlineVariant
        val labelStyle = MaterialTheme.typography.labelSmall.copy(color = onSurface)
        LineChart(
            data = listOf(
                Line(
                    label = "Saldo acumulado",
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
                xAxisProperties = GridProperties.AxisProperties(color = SolidColor(outlineVariant)),
                yAxisProperties = GridProperties.AxisProperties(color = SolidColor(outlineVariant)),
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

@Composable
private fun MonthlyBarChart(data: List<MonthlyAmount>) {
    val cashFlowColors = AppColors.colors
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Mês a mês",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        val onSurface = MaterialTheme.colorScheme.onSurface
        val outlineVariant = MaterialTheme.colorScheme.outlineVariant
        val labelStyle = MaterialTheme.typography.labelSmall.copy(color = onSurface)
        ColumnChart(
            data = data.map { month ->
                Bars(
                    label = month.month.namePtBr().take(3).replaceFirstChar { it.uppercase() },
                    values = listOf(
                        Bars.Data(value = month.income, color = SolidColor(cashFlowColors.income)),
                        Bars.Data(value = month.expenses, color = SolidColor(cashFlowColors.expense)),
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
                xAxisProperties = GridProperties.AxisProperties(color = SolidColor(outlineVariant)),
                yAxisProperties = GridProperties.AxisProperties(color = SolidColor(outlineVariant)),
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

@Composable
private fun CategoryBreakdownSection(
    title: String,
    items: List<CategoryAmount>,
    color: Color,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
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
        EmptyState("Nenhuma transação nesta categoria")
        return
    }

    val maxAmount = items.maxOf { it.amount }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { item ->
            CategoryAmountRow(
                item = item,
                maxAmount = maxAmount,
                color = color
            )
            HorizontalDivider()
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
            .padding(vertical = 4.dp),
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
                    text = "${"%.2f".format(item.amount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                )
                Text(
                    text = "${item.count} transaç${if (item.count == 1) "ão" else "ões"}",
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
        IconButton(onClick = {
            val prev = if (month == 1) Pair(year - 1, 12) else Pair(year, month - 1)
            onAction(ReportAction.DreMonthChanged(prev.first, prev.second))
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mês anterior")
        }
        Text(
            text = "$monthName $year",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        IconButton(onClick = {
            val next = if (month == 12) Pair(year + 1, 1) else Pair(year, month + 1)
            onAction(ReportAction.DreMonthChanged(next.first, next.second))
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Próximo mês")
        }
    }
}

@Composable
private fun DreStatement(dre: DreReport) {
    val cashFlowColors = AppColors.colors
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        DreSection(
            sign = "(+)",
            label = "Receita Bruta",
            item = dre.grossRevenue,
            color = cashFlowColors.income,
        )
        DreResultRow(
            label = "(-) Deduções",
            item = dre.deductions,
            color = cashFlowColors.expense
        )
        DreTotalRow(
            label = "(=) Receita Líquida",
            amount = dre.netRevenue
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        DreResultRow(
            label = "(-) Custos",
            item = dre.costs,
            color = cashFlowColors.expense
        )
        DreTotalRow(
            label = "(=) Lucro Bruto",
            amount = dre.grossProfit
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        DreSection(
            sign = "(-)",
            label = "Despesas Operacionais",
            item = dre.operationalExpenses,
            color = cashFlowColors.expense,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        DreTotalRow(
            label = "(=) Lucro / Prejuízo",
            amount = dre.netResult,
            highlight = true,
        )
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
                .padding(vertical = 8.dp),
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
                text = "${"%.2f".format(item.total)}",
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${"%.2f".format(line.amount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${"%.2f".format(item.total)}",
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
            .padding(vertical = 8.dp),
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
            text = "${"%.2f".format(amount)}",
            style = if (highlight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}
