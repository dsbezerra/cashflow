package com.github.dsbezerra.cashflow.core.di

import com.github.dsbezerra.cashflow.core.data.db.enumColumnAdapter
import com.github.dsbezerra.cashflow.core.data.seeding.DatabaseSeeder
import com.github.dsbezerra.cashflow.db.Budget
import com.github.dsbezerra.cashflow.db.CashFlowDatabase
import com.github.dsbezerra.cashflow.db.CashTransaction
import com.github.dsbezerra.cashflow.db.Account
import com.github.dsbezerra.cashflow.db.Category
import com.github.dsbezerra.cashflow.db.RecurringRule
import com.github.dsbezerra.cashflow.core.domain.model.AccountType
import com.github.dsbezerra.cashflow.core.domain.model.BudgetPeriod
import com.github.dsbezerra.cashflow.core.domain.model.CategoryType
import com.github.dsbezerra.cashflow.core.domain.model.DreClassification
import com.github.dsbezerra.cashflow.core.domain.model.Frequency
import com.github.dsbezerra.cashflow.core.data.db.DriverFactory
import com.github.dsbezerra.cashflow.core.domain.model.TransactionType
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
                dreClassificationAdapter = enumColumnAdapter(DreClassification::valueOf),
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
