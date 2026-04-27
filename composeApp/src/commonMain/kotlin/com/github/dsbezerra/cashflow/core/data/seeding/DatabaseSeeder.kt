package com.github.dsbezerra.cashflow.core.data.seeding

import com.github.dsbezerra.cashflow.core.domain.model.AccountType
import com.github.dsbezerra.cashflow.db.Category
import com.github.dsbezerra.cashflow.db.CategoryQueries
import com.github.dsbezerra.cashflow.core.domain.model.CategoryType
import com.github.dsbezerra.cashflow.core.domain.model.DreClassification
import com.github.dsbezerra.cashflow.db.Account
import com.github.dsbezerra.cashflow.db.AccountQueries
import kotlin.time.Clock

class DatabaseSeeder(
    private val accountQueries: AccountQueries,
    private val categoryQueries: CategoryQueries
) {

    fun seedIfEmpty() {
        if (categoryQueries.selectAll().executeAsList().isEmpty()) {
            defaultCategories.forEach { categoryQueries.insert(it) }
        }

        if (accountQueries.selectAll().executeAsList().isEmpty()) {
            accountQueries.insert(
                Account(
                    id = "acc_cash",
                    name = "Principal",
                    type = AccountType.CHECKING,
                    isDefault = true,
                    isArchived = false,
                    initialBalance = 0.0,
                    currency = "BRL",
                    icon = "attach_money",
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                )
            )
        }
    }

    private val defaultCategories = listOf(
        category(
            "cat_salary",
            "Salário",
            CategoryType.INCOME,
            DreClassification.GROSS_REVENUE,
            "work",
        ),
        category(
            "cat_freelance",
            "Freelance",
            CategoryType.INCOME,
            DreClassification.GROSS_REVENUE,
            "work",
        ),
        category(
            "cat_sales",
            "Receita de Vendas",
            CategoryType.INCOME,
            DreClassification.GROSS_REVENUE,
            "shopping_cart",
        ),
        category(
            "cat_investment",
            "Investimentos",
            CategoryType.INCOME,
            DreClassification.NONE,
            "trending_up",
        ),
        category(
            "cat_other_income",
            "Outros rendimentos",
            CategoryType.INCOME,
            DreClassification.GROSS_REVENUE,
            "attach_money",
        ),
        category(
            "cat_taxes",
            "Impostos",
            CategoryType.EXPENSE,
            DreClassification.DEDUCTION,
            "payments",
        ),
        category(
            "cat_returns",
            "Devoluções",
            CategoryType.EXPENSE,
            DreClassification.DEDUCTION,
            "shopping_cart",
        ),
        category(
            "cat_cogs",
            "Custo de Mercadoria",
            CategoryType.EXPENSE,
            DreClassification.COST,
            "shopping_cart",
        ),
        category(
            "cat_food",
            "Alimentação",
            CategoryType.EXPENSE,
            DreClassification.EXPENSE,
            "restaurant",
        ),
        category(
            "cat_transport",
            "Transporte",
            CategoryType.EXPENSE,
            DreClassification.EXPENSE,
            "directions_car",
        ),
        category(
            "cat_housing",
            "Moradia",
            CategoryType.EXPENSE,
            DreClassification.EXPENSE,
            "home",
        ),
        category(
            "cat_health",
            "Saúde",
            CategoryType.EXPENSE,
            DreClassification.EXPENSE,
            "local_hospital",
        ),
        category(
            "cat_leisure",
            "Lazer",
            CategoryType.EXPENSE,
            DreClassification.EXPENSE,
            "sports_esports",
        ),
        category(
            "cat_education",
            "Educação",
            CategoryType.EXPENSE,
            DreClassification.EXPENSE,
            "school",
        ),
        category(
            "cat_clothing",
            "Vestuário",
            CategoryType.EXPENSE,
            DreClassification.EXPENSE,
            "shopping_cart",
        ),
        category(
            "cat_subscriptions",
            "Assinaturas",
            CategoryType.EXPENSE,
            DreClassification.EXPENSE,
            "payments",
        ),
        category(
            "cat_other_expense",
            "Outros gastos",
            CategoryType.EXPENSE,
            DreClassification.EXPENSE,
            "favorite",
        ),
        category(
            "cat_transfer",
            "Transferência",
            CategoryType.BOTH,
            DreClassification.NONE,
            "swap_horiz",
        ),
    )

    private fun category(
        id: String,
        name: String,
        type: CategoryType,
        dreClassification: DreClassification,
        icon: String,
    ) = Category(
        id = id,
        name = name,
        type = type,
        dreClassification = dreClassification,
        icon = icon,
        parentId = null,
        isDefault = true,
        isArchived = false,
    )
}
