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
        DashboardViewModel(
            get(),
            get(),
            get()
        )
    }
    viewModel {
        TransactionListViewModel(
            get(),
            get()
        )
    }
    viewModel {
        TransactionDetailViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel {
        AccountListViewModel(
            get(),
            get()
        )
    }
    viewModel {
        AccountDetailViewModel(
            get(),
            get()
        )
    }
    viewModel { AccountFormViewModel(get()) }
    viewModel { CategoryListViewModel(get()) }
    viewModel { CategoryFormViewModel(get()) }
    viewModel { ReportViewModel(get(), get(), get()) }
    viewModel { RecurringRuleListViewModel(get()) }
    viewModel {
        RecurringRuleFormViewModel(
            get(),
            get(),
            get()
        )
    }
}
