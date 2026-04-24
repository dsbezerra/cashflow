package com.github.dsbezerra.cashflow.di

import com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailViewModel
import com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormViewModel
import com.github.dsbezerra.cashflow.ui.screens.accounts.AccountListViewModel
import com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormViewModel
import com.github.dsbezerra.cashflow.ui.screens.categories.CategoryListViewModel
import com.github.dsbezerra.cashflow.ui.screens.dashboard.DashboardViewModel
import com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormViewModel
import com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListViewModel
import com.github.dsbezerra.cashflow.ui.screens.reports.ReportViewModel
import com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailViewModel
import com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        com.github.dsbezerra.cashflow.ui.screens.dashboard.DashboardViewModel(
            get(),
            get(),
            get()
        )
    }
    viewModel {
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionListViewModel(
            get(),
            get()
        )
    }
    viewModel {
        com.github.dsbezerra.cashflow.ui.screens.transactions.TransactionDetailViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel {
        com.github.dsbezerra.cashflow.ui.screens.accounts.AccountListViewModel(
            get(),
            get()
        )
    }
    viewModel {
        com.github.dsbezerra.cashflow.ui.screens.accounts.AccountDetailViewModel(
            get(),
            get()
        )
    }
    viewModel { com.github.dsbezerra.cashflow.ui.screens.accounts.AccountFormViewModel(get()) }
    viewModel { com.github.dsbezerra.cashflow.ui.screens.categories.CategoryListViewModel(get()) }
    viewModel { com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormViewModel(get()) }
    viewModel { com.github.dsbezerra.cashflow.ui.screens.reports.ReportViewModel(get(), get(), get()) }
    viewModel { com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleListViewModel(get()) }
    viewModel {
        com.github.dsbezerra.cashflow.ui.screens.recurring.RecurringRuleFormViewModel(
            get(),
            get(),
            get()
        )
    }
}
