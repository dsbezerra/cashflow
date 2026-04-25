package com.github.dsbezerra.cashflow.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.github.dsbezerra.cashflow.feature.account.detail.AccountDetailScreen
import com.github.dsbezerra.cashflow.feature.account.form.AccountFormScreen
import com.github.dsbezerra.cashflow.feature.account.list.AccountListScreen
import com.github.dsbezerra.cashflow.feature.category.form.CategoryFormScreen
import com.github.dsbezerra.cashflow.feature.category.list.CategoryListScreen
import com.github.dsbezerra.cashflow.feature.dashboard.DashboardScreen
import com.github.dsbezerra.cashflow.feature.about.AboutScreen
import com.github.dsbezerra.cashflow.feature.recurring.form.RecurringRuleFormScreen
import com.github.dsbezerra.cashflow.feature.recurring.list.RecurringRuleListScreen
import com.github.dsbezerra.cashflow.feature.report.ReportScreen
import com.github.dsbezerra.cashflow.feature.settings.SettingsScreen
import com.github.dsbezerra.cashflow.feature.transaction.detail.TransactionDetailScreen
import com.github.dsbezerra.cashflow.feature.transaction.list.TransactionListScreen

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
                onSeeAll = {
                    navController.navigate(TransactionList) {
                        popUpTo(Dashboard) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
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
