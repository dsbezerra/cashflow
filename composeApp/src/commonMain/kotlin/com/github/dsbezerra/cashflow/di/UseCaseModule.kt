package com.github.dsbezerra.cashflow.di

import com.github.dsbezerra.cashflow.domain.usecase.account.GetAccountBalanceUseCase
import com.github.dsbezerra.cashflow.domain.usecase.budget.CheckBudgetThresholdUseCase
import com.github.dsbezerra.cashflow.domain.usecase.dashboard.GetDashboardSummaryUseCase
import com.github.dsbezerra.cashflow.domain.usecase.recurring.GenerateRecurringTransactionsUseCase
import com.github.dsbezerra.cashflow.domain.usecase.transaction.CreateTransactionUseCase
import com.github.dsbezerra.cashflow.domain.usecase.transaction.CreateTransferUseCase
import com.github.dsbezerra.cashflow.domain.usecase.report.GetDreReportUseCase
import com.github.dsbezerra.cashflow.domain.usecase.report.GetReportUseCase
import com.github.dsbezerra.cashflow.domain.usecase.transaction.DeleteTransactionUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { CreateTransactionUseCase(get()) }
    factory { CreateTransferUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { GetAccountBalanceUseCase(get()) }
    factory { GetDashboardSummaryUseCase(get(), get()) }
    factory { CheckBudgetThresholdUseCase() }
    factory { GenerateRecurringTransactionsUseCase(get(), get()) }
    factory { GetReportUseCase(get(), get()) }
    factory { GetDreReportUseCase(get(), get()) }
}
