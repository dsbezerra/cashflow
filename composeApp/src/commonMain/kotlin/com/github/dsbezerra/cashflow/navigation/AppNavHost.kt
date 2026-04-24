package com.github.dsbezerra.cashflow.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.github.dsbezerra.cashflow.ui.accounts.AccountDetailScreen
import com.github.dsbezerra.cashflow.ui.accounts.AccountFormScreen
import com.github.dsbezerra.cashflow.ui.accounts.AccountListScreen
import com.github.dsbezerra.cashflow.ui.categories.CategoryFormScreen
import com.github.dsbezerra.cashflow.ui.categories.CategoryListScreen
import com.github.dsbezerra.cashflow.ui.dashboard.DashboardScreen
import com.github.dsbezerra.cashflow.ui.about.AboutScreen
import com.github.dsbezerra.cashflow.ui.recurring.RecurringRuleFormScreen
import com.github.dsbezerra.cashflow.ui.recurring.RecurringRuleListScreen
import com.github.dsbezerra.cashflow.ui.reports.ReportScreen
import com.github.dsbezerra.cashflow.ui.settings.SettingsScreen
import com.github.dsbezerra.cashflow.ui.transactions.TransactionDetailScreen
import com.github.dsbezerra.cashflow.ui.transactions.TransactionListScreen

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
            DashboardScreen(
                onNavigateToTransaction = { id ->
                    navController.navigate(TransactionDetail(transactionId = id))
                },
            )
        }
        composable<TransactionList> {
            TransactionListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(TransactionDetail(transactionId = id))
                },
            )
        }
        composable<TransactionDetail> { entry ->
            val route = entry.toRoute<TransactionDetail>()
            TransactionDetailScreen(
                transactionId = route.transactionId,
                defaultAccountId = route.defaultAccountId,
                defaultType = route.defaultType,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable<Accounts> {
            AccountListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(AccountDetail(accountId = id))
                },
            )
        }
        composable<AccountDetail> { entry ->
            val route = entry.toRoute<AccountDetail>()
            AccountDetailScreen(
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
            AccountFormScreen(
                accountId = route.accountId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable<Settings> {
            SettingsScreen(
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
            CategoryListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForm = { id ->
                    navController.navigate(CategoryForm(categoryId = id))
                },
            )
        }
        composable<CategoryForm> { entry ->
            val route = entry.toRoute<CategoryForm>()
            CategoryFormScreen(
                categoryId = route.categoryId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable<Reports> {
            ReportScreen()
        }
        composable<RecurringRuleList> {
            RecurringRuleListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForm = { id ->
                    navController.navigate(RecurringRuleForm(ruleId = id))
                },
            )
        }
        composable<RecurringRuleForm> { entry ->
            val route = entry.toRoute<RecurringRuleForm>()
            RecurringRuleFormScreen(
                ruleId = route.ruleId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable<About> {
            AboutScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
