package com.github.dsbezerra.cashflow.ui.shell

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.github.dsbezerra.cashflow.isDesktop
import com.github.dsbezerra.cashflow.navigation.Accounts
import com.github.dsbezerra.cashflow.navigation.Dashboard
import com.github.dsbezerra.cashflow.navigation.Settings
import com.github.dsbezerra.cashflow.navigation.TransactionList

private val navItems = listOf(
    NavItem("Painel", Icons.Default.Dashboard, Dashboard),
    NavItem("Transações", Icons.Default.List, TransactionList),
    NavItem("Contas", Icons.Default.AccountBalance, Accounts),
    NavItem("Configurações", Icons.Default.Settings, Settings),
)

@Composable
fun AppShell(
    navController: NavController,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (Modifier) -> Unit,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    if (isDesktop) {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail {
                navItems.forEach { item ->
                    val selected = currentDestination?.hasRoute(item.route::class) == true
                    NavigationRailItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(item.route) {
                                    popUpTo(Dashboard) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.label)
                        },
                        label = { Text(item.label) },
                    )
                }
            }
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                content(Modifier.fillMaxSize())
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp),
                ) {
                    floatingActionButton()
                }
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    navItems.forEach { item ->
                        val selected = currentDestination?.hasRoute(item.route::class) == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(Dashboard) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(item.icon, contentDescription = item.label)
                            },
                            label = { Text(item.label) },
                        )
                    }
                }
            },
            floatingActionButton = floatingActionButton,
        ) { innerPadding ->
            content(Modifier.padding(innerPadding))
        }
    }
}
