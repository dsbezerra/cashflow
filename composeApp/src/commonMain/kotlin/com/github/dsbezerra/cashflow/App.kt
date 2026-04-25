package com.github.dsbezerra.cashflow

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.github.dsbezerra.cashflow.ui.theme.CashFlowTheme
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.dsbezerra.cashflow.domain.usecase.recurring.GenerateRecurringTransactionsUseCase
import com.github.dsbezerra.cashflow.navigation.AccountForm
import com.github.dsbezerra.cashflow.navigation.Accounts
import com.github.dsbezerra.cashflow.navigation.AppNavHost
import com.github.dsbezerra.cashflow.navigation.Dashboard
import com.github.dsbezerra.cashflow.navigation.TransactionDetail
import com.github.dsbezerra.cashflow.navigation.TransactionList
import com.github.dsbezerra.cashflow.ui.shell.AppShell
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
                            onClick = {
                                navController.navigate(TransactionDetail())
                            }
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
                            onClick = {
                                navController.navigate(AccountForm())
                            }
                        )
                    }
                }
            },
        ) { contentModifier ->
            AppNavHost(navController = navController, modifier = contentModifier)
        }
    }
}
