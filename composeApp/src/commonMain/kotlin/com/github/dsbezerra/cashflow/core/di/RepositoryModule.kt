package com.github.dsbezerra.cashflow.core.di

import com.github.dsbezerra.cashflow.core.data.repository.SqlDelightAccountRepository
import com.github.dsbezerra.cashflow.core.data.repository.SqlDelightBudgetRepository
import com.github.dsbezerra.cashflow.core.data.repository.SqlDelightCategoryRepository
import com.github.dsbezerra.cashflow.core.data.repository.SqlDelightRecurringRuleRepository
import com.github.dsbezerra.cashflow.core.data.repository.SqlDelightTransactionRepository
import com.github.dsbezerra.cashflow.core.domain.repository.AccountRepository
import com.github.dsbezerra.cashflow.core.domain.repository.BudgetRepository
import com.github.dsbezerra.cashflow.core.domain.repository.CategoryRepository
import com.github.dsbezerra.cashflow.core.domain.repository.RecurringRuleRepository
import com.github.dsbezerra.cashflow.core.domain.repository.TransactionRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AccountRepository> { SqlDelightAccountRepository(get()) }
    single<TransactionRepository> { SqlDelightTransactionRepository(get()) }
    single<CategoryRepository> { SqlDelightCategoryRepository(get()) }
    single<RecurringRuleRepository> { SqlDelightRecurringRuleRepository(get()) }
    single<BudgetRepository> { SqlDelightBudgetRepository(get()) }
}
