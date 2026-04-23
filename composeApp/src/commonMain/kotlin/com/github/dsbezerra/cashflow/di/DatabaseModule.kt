package com.github.dsbezerra.cashflow.di

import com.github.dsbezerra.cashflow.data.db.enumColumnAdapter
import com.github.dsbezerra.cashflow.data.seeding.DatabaseSeeder
import com.github.dsbezerra.cashflow.db.Budget
import com.github.dsbezerra.cashflow.db.CashFlowDatabase
import com.github.dsbezerra.cashflow.db.CashTransaction
import com.github.dsbezerra.cashflow.db.Account
import com.github.dsbezerra.cashflow.db.Category
import com.github.dsbezerra.cashflow.db.RecurringRule
import com.github.dsbezerra.cashflow.domain.model.AccountType
import com.github.dsbezerra.cashflow.domain.model.BudgetPeriod
import com.github.dsbezerra.cashflow.domain.model.CategoryType
import com.github.dsbezerra.cashflow.domain.model.Frequency
import com.github.dsbezerra.cashflow.data.db.DriverFactory
import com.github.dsbezerra.cashflow.domain.model.TransactionType
import org.koin.dsl.module

val databaseModule = module {
    single { get<DriverFactory>().createDriver() }
    single {
        CashFlowDatabase(
            driver = get(),
            AccountAdapter = Account.Adapter(
                typeAdapter = enumColumnAdapter(AccountType::valueOf),
            ),
            CashTransactionAdapter = CashTransaction.Adapter(
                typeAdapter = enumColumnAdapter(TransactionType::valueOf),
            ),
            CategoryAdapter = Category.Adapter(
                typeAdapter = enumColumnAdapter(CategoryType::valueOf),
            ),
            RecurringRuleAdapter = RecurringRule.Adapter(
                typeAdapter = enumColumnAdapter(TransactionType::valueOf),
                frequencyAdapter = enumColumnAdapter(Frequency::valueOf),
            ),
            BudgetAdapter = Budget.Adapter(
                periodAdapter = enumColumnAdapter(BudgetPeriod::valueOf),
            ),
        ).also { db -> DatabaseSeeder(db.categoryQueries).seedIfEmpty() }
    }
    single { get<CashFlowDatabase>().accountQueries }
    single { get<CashFlowDatabase>().categoryQueries }
    single { get<CashFlowDatabase>().transactionQueries }
    single { get<CashFlowDatabase>().recurringRuleQueries }
    single { get<CashFlowDatabase>().budgetQueries }
}
