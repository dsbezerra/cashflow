package com.github.dsbezerra.cashflow

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.github.dsbezerra.cashflow.core.designsystem.theme.CashFlowTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.dsbezerra.cashflow.core.domain.usecase.recurring.GenerateRecurringTransactionsUseCase
import com.github.dsbezerra.cashflow.core.navigation.Accounts
import com.github.dsbezerra.cashflow.core.navigation.AppNavHost
import com.github.dsbezerra.cashflow.core.navigation.Dashboard
import com.github.dsbezerra.cashflow.core.navigation.TransactionList
import com.github.dsbezerra.cashflow.core.navigation.AppShell
import com.github.dsbezerra.cashflow.feature.account.form.AccountFormSheet
import com.github.dsbezerra.cashflow.feature.transaction.detail.TransactionFormSheet
import kotlin.time.Clock
import org.koin.compose.koinInject

@Composable
fun App() {
    CashFlowTheme {
        val generateRecurring: GenerateRecurringTransactionsUseCase = koinInject()
        LaunchedEffect(Unit) {
            generateRecurring(Clock.System.now().toEpochMilliseconds())
        }

        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backStackEntry?.destination

        val isOnDashboard = currentDestination?.hasRoute(Dashboard::class) == true
        val isOnTransactions = currentDestination?.hasRoute(TransactionList::class) == true
        val isOnAccounts = currentDestination?.hasRoute(Accounts::class) == true

        var showTransactionSheet by remember { mutableStateOf(false) }
        var showAccountSheet by remember { mutableStateOf(false) }

        AppShell(
            navController = navController,
            floatingActionButton = {
                when {
                    isOnDashboard || isOnTransactions -> {
                        ExtendedFloatingActionButton(
                            text = {
                                Text("Nova transação")
                            },
                            icon = {
                                Icon(Icons.Default.Add, contentDescription = "Nova transação")
                            },
                            onClick = { showTransactionSheet = true }
                        )
                    }

                    isOnAccounts -> {
                        ExtendedFloatingActionButton(
                            text = {
                                Text("Nova conta")
                            },
                            icon = {
                                Icon(Icons.Default.Add, contentDescription = "Nova conta")
                            },
                            onClick = { showAccountSheet = true }
                        )
                    }
                }
            },
        ) { contentModifier ->
            AppNavHost(navController = navController, modifier = contentModifier)
        }

        if (showTransactionSheet) {
            TransactionFormSheet(onDismiss = { showTransactionSheet = false })
        }
        if (showAccountSheet) {
            AccountFormSheet(onDismiss = { showAccountSheet = false })
        }
    }
}
