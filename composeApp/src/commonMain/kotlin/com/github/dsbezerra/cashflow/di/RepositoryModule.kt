package com.github.dsbezerra.cashflow.di

import com.github.dsbezerra.cashflow.data.repository.SqlDelightAccountRepository
import com.github.dsbezerra.cashflow.data.repository.SqlDelightBudgetRepository
import com.github.dsbezerra.cashflow.data.repository.SqlDelightCategoryRepository
import com.github.dsbezerra.cashflow.data.repository.SqlDelightRecurringRuleRepository
import com.github.dsbezerra.cashflow.data.repository.SqlDelightTransactionRepository
import com.github.dsbezerra.cashflow.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.domain.repository.BudgetRepository
import com.github.dsbezerra.cashflow.domain.repository.CategoryRepository
import com.github.dsbezerra.cashflow.domain.repository.RecurringRuleRepository
import com.github.dsbezerra.cashflow.domain.repository.TransactionRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AccountRepository> { SqlDelightAccountRepository(get()) }
    single<TransactionRepository> { SqlDelightTransactionRepository(get()) }
    single<CategoryRepository> { SqlDelightCategoryRepository(get()) }
    single<RecurringRuleRepository> { SqlDelightRecurringRuleRepository(get()) }
    single<BudgetRepository> { SqlDelightBudgetRepository(get()) }
}
