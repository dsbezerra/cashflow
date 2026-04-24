package com.github.dsbezerra.cashflow.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailScreen
import com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormScreen
import com.github.dsbezerra.cashflow.ui.screens.accounts.AccountListScreen
import com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormScreen
import com.github.dsbezerra.cashflow.ui.screens.categories.CategoryListScreen
import com.github.dsbezerra.cashflow.ui.screens.dashboard.DashboardScreen
import com.github.dsbezerra.cashflow.ui.screens.about.AboutScreen
import com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormScreen
import com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListScreen
import com.github.dsbezerra.cashflow.ui.screens.reports.ReportScreen
import com.github.dsbezerra.cashflow.ui.screens.settings.SettingsScreen
import com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailScreen
import com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionListScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Dashboard,
        modifier = modifier,
    ) {
        composable<Dashboard> {
            com.github.dsbezerra.cashflow.ui.screens.dashboard.DashboardScreen(
                onNavigateToTransaction = { id ->
                    navController.navigate(TransactionDetail(transactionId = id))
                },
            )
        }
        composable<TransactionList> {
            com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(TransactionDetail(transactionId = id))
                },
            )
        }
        composable<TransactionDetail> { entry ->
            val route = entry.toRoute<TransactionDetail>()
            com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailScreen(
                transactionId = route.transactionId,
                defaultAccountId = route.defaultAccountId,
                defaultType = route.defaultType,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable<Accounts> {
            com.github.dsbezerra.cashflow.ui.screens.accounts.AccountListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(AccountDetail(accountId = id))
                },
            )
        }
        composable<AccountDetail> { entry ->
            val route = entry.toRoute<AccountDetail>()
            com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailScreen(
                accountId = route.accountId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(AccountForm(accountId = id))
                },
                onNavigateToTransfer = { accountId ->
                    navController.navigate(
                        TransactionDetail(
                            defaultAccountId = accountId,
                            defaultType = "TRANSFER",
                        )
                    )
                },
            )
        }
        composable<AccountForm> { entry ->
            val route = entry.toRoute<AccountForm>()
            com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormScreen(
                accountId = route.accountId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable<Settings> {
            com.github.dsbezerra.cashflow.ui.screens.settings.SettingsScreen(
                onNavigateToCategoryList = {
                    navController.navigate(CategoryList)
                },
                onNavigateToRecurringList = {
                    navController.navigate(RecurringRuleList)
                },
                onNavigateToAbout = {
                    navController.navigate(About)
                },
            )
        }
        composable<CategoryList> {
            com.github.dsbezerra.cashflow.ui.screens.categories.CategoryListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForm = { id ->
                    navController.navigate(CategoryForm(categoryId = id))
                },
            )
        }
        composable<CategoryForm> { entry ->
            val route = entry.toRoute<CategoryForm>()
            com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormScreen(
                categoryId = route.categoryId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable<Reports> {
            com.github.dsbezerra.cashflow.ui.screens.reports.ReportScreen()
        }
        composable<RecurringRuleList> {
            com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForm = { id ->
                    navController.navigate(RecurringRuleForm(ruleId = id))
                },
            )
        }
        composable<RecurringRuleForm> { entry ->
            val route = entry.toRoute<RecurringRuleForm>()
            com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormScreen(
                ruleId = route.ruleId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable<About> {
            com.github.dsbezerra.cashflow.ui.screens.about.AboutScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
