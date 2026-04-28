package com.github.dsbezerra.cashflow.feature.scenario.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cashflow.composeapp.generated.resources.Res
import cashflow.composeapp.generated.resources.account_label
import cashflow.composeapp.generated.resources.back
import cashflow.composeapp.generated.resources.calculator_amount_per_item
import cashflow.composeapp.generated.resources.calculator_current_balance
import cashflow.composeapp.generated.resources.calculator_difference
import cashflow.composeapp.generated.resources.calculator_projected_balance
import cashflow.composeapp.generated.resources.calculator_quantity
import cashflow.composeapp.generated.resources.calculator_title
import cashflow.composeapp.generated.resources.expense
import cashflow.composeapp.generated.resources.income
import com.github.dsbezerra.cashflow.core.designsystem.component.ChipSelector
import com.github.dsbezerra.cashflow.core.designsystem.component.CurrencyTextField
import com.github.dsbezerra.cashflow.core.designsystem.component.DSFullscreenLoader
import com.github.dsbezerra.cashflow.core.designsystem.component.IconButtonWithTooltip
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
import com.github.dsbezerra.cashflow.core.domain.model.toDecimal
import com.github.dsbezerra.cashflow.core.designsystem.theme.AppColors
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioCalculatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: ScenarioCalculatorViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ScenarioCalculatorEvent.NavigateBack -> onNavigateBack()
                is ScenarioCalculatorEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.calculator_title)) },
                navigationIcon = {
                    IconButtonWithTooltip(
                        onClick = onNavigateBack,
                        tooltip = stringResource(Res.string.back),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        if (state.isLoading) {
            DSFullscreenLoader()
        } else {
            ScenarioCalculatorBody(
                state = state,
                onAction = viewModel::onAction,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun ScenarioCalculatorBody(
    state: ScenarioCalculatorState,
    onAction: (ScenarioCalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cashFlowColors = AppColors.colors
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Account selector
        if (state.accounts.isNotEmpty()) {
            ChipSelector(
                label = stringResource(Res.string.account_label),
                selectedId = state.selectedAccountId,
                items = state.accounts.map { it.id to it.name },
                onSelect = { onAction(ScenarioCalculatorAction.AccountSelected(it)) },
            )
        }

        // Type selector
        val types = listOf(TransactionType.INCOME, TransactionType.EXPENSE)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            types.forEachIndexed { index, type ->
                SegmentedButton(
                    selected = state.type == type,
                    onClick = { onAction(ScenarioCalculatorAction.TypeChanged(type)) },
                    shape = SegmentedButtonDefaults.itemShape(index, types.size),
                    label = {
                        Text(
                            when (type) {
                                TransactionType.INCOME -> stringResource(Res.string.income)
                                else -> stringResource(Res.string.expense)
                            }
                        )
                    },
                )
            }
        }

        // Amount per item
        CurrencyTextField(
            value = state.amountPerItemCents,
            onValueChange = { onAction(ScenarioCalculatorAction.AmountChanged(it)) },
            label = stringResource(Res.string.calculator_amount_per_item),
            modifier = Modifier.fillMaxWidth(),
        )

        // Quantity
        OutlinedTextField(
            value = state.quantity.toString(),
            onValueChange = { raw ->
                val q = raw.filter { it.isDigit() }.toIntOrNull() ?: 1
                onAction(ScenarioCalculatorAction.QuantityChanged(q))
            },
            label = { Text(stringResource(Res.string.calculator_quantity)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // Results card
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ResultRow(
                    label = stringResource(Res.string.calculator_current_balance),
                    value = state.currentBalance.toDecimal().toCurrency(),
                    valueColor = MaterialTheme.colorScheme.onSurface,
                )
                HorizontalDivider()
                ResultRow(
                    label = stringResource(Res.string.calculator_projected_balance),
                    value = state.projectedBalance.toDecimal().toCurrency(),
                    valueColor = if (state.type == TransactionType.INCOME) cashFlowColors.income else cashFlowColors.expense,
                    bold = true,
                )
                ResultRow(
                    label = stringResource(Res.string.calculator_difference),
                    value = (if (state.difference >= 0) "+" else "") + state.difference.toDecimal().toCurrency(),
                    valueColor = if (state.difference >= 0) cashFlowColors.income else cashFlowColors.expense,
                )
            }
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    valueColor: Color,
    bold: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = if (bold) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold,
            color = valueColor,
        )
    }
}
