package com.github.dsbezerra.cashflow.ui.recurring

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
import androidx.compose.material3.IconButton
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
import com.github.dsbezerra.cashflow.domain.model.Frequency
import com.github.dsbezerra.cashflow.domain.model.RecurringRule
import com.github.dsbezerra.cashflow.domain.model.TransactionType
import com.github.dsbezerra.cashflow.ui.common.DesktopVerticalScrollbar
import com.github.dsbezerra.cashflow.util.formatFullPtBr
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

private val incomeColor = Color(0xFF4CAF50)
private val expenseColor = Color(0xFFF44336)

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
                title = { Text("Transações Recorrentes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToForm(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar regra")
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
                        Text("Nenhuma regra recorrente", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Toque em + para adicionar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                else -> {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                        items(state.rules, key = { it.id }) { rule ->
                            RecurringRuleRow(
                                rule = rule,
                                onClick = { onNavigateToForm(rule.id) },
                                onToggleActive = { isActive ->
                                    viewModel.onAction(RecurringRuleListAction.ToggleActive(rule.id, isActive))
                                },
                            )
                            HorizontalDivider()
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
    val tz = TimeZone.currentSystemDefault()
    val nextDate = Instant.fromEpochMilliseconds(rule.nextOccurrence)
        .toLocalDateTime(tz).date
    val amountColor = when (rule.type) {
        TransactionType.INCOME -> incomeColor
        TransactionType.EXPENSE -> expenseColor
        TransactionType.TRANSFER -> Color.Unspecified
    }
    val frequencyLabel = "${rule.frequency.labelPtBr()} · a cada ${rule.interval} ${rule.frequency.unitPtBr(rule.interval)}"

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
                    text = "${"%.2f".format(rule.amount)}",
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
                text = "Próximo: ${nextDate.formatFullPtBr()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        FilterChip(
            selected = rule.isActive,
            onClick = { onToggleActive(!rule.isActive) },
            label = { Text(if (rule.isActive) "Ativo" else "Pausado") },
            modifier = Modifier.padding(start = 8.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                selectedLabelColor = Color(0xFF4CAF50),
            ),
        )
    }
}

private fun Frequency.labelPtBr(): String = when (this) {
    Frequency.DAILY -> "Diário"
    Frequency.WEEKLY -> "Semanal"
    Frequency.MONTHLY -> "Mensal"
    Frequency.YEARLY -> "Anual"
}

private fun Frequency.unitPtBr(interval: Int): String = when (this) {
    Frequency.DAILY -> if (interval == 1) "dia" else "dias"
    Frequency.WEEKLY -> if (interval == 1) "semana" else "semanas"
    Frequency.MONTHLY -> if (interval == 1) "mês" else "meses"
    Frequency.YEARLY -> if (interval == 1) "ano" else "anos"
}
