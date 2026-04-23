package com.github.dsbezerra.cashflow.di

import com.github.dsbezerra.cashflow.ui.accounts.AccountDetailViewModel
import com.github.dsbezerra.cashflow.ui.accounts.AccountFormViewModel
import com.github.dsbezerra.cashflow.ui.accounts.AccountListViewModel
import com.github.dsbezerra.cashflow.ui.categories.CategoryFormViewModel
import com.github.dsbezerra.cashflow.ui.categories.CategoryListViewModel
import com.github.dsbezerra.cashflow.ui.dashboard.DashboardViewModel
import com.github.dsbezerra.cashflow.ui.reports.ReportViewModel
import com.github.dsbezerra.cashflow.ui.transactions.TransactionDetailViewModel
import com.github.dsbezerra.cashflow.ui.transactions.TransactionListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { TransactionListViewModel(get(), get()) }
    viewModel { TransactionDetailViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { AccountListViewModel(get(), get()) }
    viewModel { AccountDetailViewModel(get(), get()) }
    viewModel { AccountFormViewModel(get()) }
    viewModel { CategoryListViewModel(get()) }
    viewModel { CategoryFormViewModel(get()) }
    viewModel { ReportViewModel(get(), get()) }
}
