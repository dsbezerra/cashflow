package com.github.dsbezerra.cashflow.core.di

import com.github.dsbezerra.cashflow.feature.account.detail.AccountDetailViewModel
import com.github.dsbezerra.cashflow.feature.account.form.AccountFormViewModel
import com.github.dsbezerra.cashflow.feature.account.list.AccountListViewModel
import com.github.dsbezerra.cashflow.feature.category.form.CategoryFormViewModel
import com.github.dsbezerra.cashflow.feature.category.list.CategoryListViewModel
import com.github.dsbezerra.cashflow.feature.dashboard.DashboardViewModel
import com.github.dsbezerra.cashflow.feature.recurring.form.RecurringRuleFormViewModel
import com.github.dsbezerra.cashflow.feature.recurring.list.RecurringRuleListViewModel
import com.github.dsbezerra.cashflow.feature.report.ReportViewModel
import com.github.dsbezerra.cashflow.feature.transaction.detail.TransactionDetailViewModel
import com.github.dsbezerra.cashflow.feature.transaction.list.TransactionListViewModel
import com.github.dsbezerra.cashflow.feature.scenario.calculator.ScenarioCalculatorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        DashboardViewModel(
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel {
        TransactionListViewModel(
            get(),
            get(),
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
            get(),
            get()
        )
    }
    viewModel {
        AccountListViewModel(
            get(),
            get(),
            get()
        )
    }
    viewModel {
        AccountDetailViewModel(
            get(),
            get(),
            get()
        )
    }
    viewModel { AccountFormViewModel(get(), get()) }
    viewModel { CategoryListViewModel(get(), get()) }
    viewModel { CategoryFormViewModel(get(), get()) }
    viewModel { ReportViewModel(get(), get(), get(), get()) }
    viewModel { RecurringRuleListViewModel(get(), get()) }
    viewModel {
        RecurringRuleFormViewModel(
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel { ScenarioCalculatorViewModel(get(), get(), get()) }
}
