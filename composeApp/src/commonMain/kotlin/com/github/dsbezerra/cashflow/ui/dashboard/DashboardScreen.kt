package com.github.dsbezerra.cashflow.ui.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import com.github.dsbezerra.cashflow.ui.common.DesktopVerticalScrollbar
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.dsbezerra.cashflow.domain.model.Category
import com.github.dsbezerra.cashflow.domain.model.DashboardSummary
import com.github.dsbezerra.cashflow.domain.model.Transaction
import com.github.dsbezerra.cashflow.domain.model.TransactionType
import com.github.dsbezerra.cashflow.ui.common.AmountText
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

private val incomeColor = Color(0xFF4CAF50)
private val expenseColor = Color(0xFFF44336)

@Composable
fun DashboardScreen(
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

    Box(Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            DashboardContent(
                state = state,
                onNavigateToTransaction = onNavigateToTransaction,
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun DashboardContent(
    state: DashboardState,
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
                SummaryCards(summary)
            }
            item {
                ExpenseDonutChart(summary, state.categories)
            }
            item {
                IncomeExpenseBarChart(summary)
            }
            item {
                RecentTransactions(
                    transactions = summary.recentTransactions,
                    onTransactionClick = { onNavigateToTransaction(it) },
                )
            }
        }
        DesktopVerticalScrollbar(rememberScrollbarAdapter(listState))
    }
}

@Composable
private fun SummaryCards(summary: DashboardSummary) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        SummaryCard(
            label = "Saldo Total",
            amount = summary.totalBalance,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            label = "Receita",
            amount = summary.monthlyIncome,
            color = incomeColor,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            label = "Despesas",
            amount = summary.monthlyExpenses,
            color = expenseColor,
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
private fun ExpenseDonutChart(
    summary: DashboardSummary,
    categories: Map<String, Category>,
) {
    val slices = summary.recentTransactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.categoryId }
        .mapNotNull { (catId, txs) ->
            val cat = categories[catId] ?: return@mapNotNull null
            Pie(
                data = txs.sumOf { it.amount },
                color = cat.color.toComposeColor(),
                label = cat.name,
            )
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Despesas por categoria",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        if (slices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Sem despesas recentes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            PieChart(
                data = slices,
                style = Pie.Style.Stroke(width = 40.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            )
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                slices.forEach { slice ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(slice.color, CircleShape),
                        )
                        Text(
                            text = slice.label ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "${"%.2f".format(slice.data)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = expenseColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IncomeExpenseBarChart(summary: DashboardSummary) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Receita vs Despesas",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        ColumnChart(
            data = listOf(
                Bars(
                    label = "Receita",
                    values = listOf(
                        Bars.Data(
                            value = summary.monthlyIncome,
                            color = SolidColor(incomeColor),
                        ),
                    ),
                ),
                Bars(
                    label = "Despesas",
                    values = listOf(
                        Bars.Data(
                            value = summary.monthlyExpenses,
                            color = SolidColor(expenseColor),
                        ),
                    ),
                ),
            ),
            barProperties = BarProperties(
                thickness = 48.dp,
                spacing = 4.dp,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        )
    }
}

@Composable
private fun RecentTransactions(
    transactions: List<Transaction>,
    onTransactionClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Transações recentes",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        if (transactions.isEmpty()) {
            Text(
                text = "Nenhuma transação recente",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        } else {
            Column {
                transactions.forEach { tx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTransactionClick(tx.id) }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = tx.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        AmountText(amount = tx.amount, type = tx.type)
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

private fun String.toComposeColor(): Color {
    val hex = removePrefix("#").padStart(8, 'F')
    return Color(hex.toLong(16).toInt())
}
