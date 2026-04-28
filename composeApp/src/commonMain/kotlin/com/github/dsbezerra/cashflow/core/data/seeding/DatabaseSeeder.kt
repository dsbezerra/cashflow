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
        // Receita Bruta
        category("cat_sales_card", "Receita de vendas (cartão)", CategoryType.INCOME, DreClassification.GROSS_REVENUE, "credit_card"),
        category("cat_sales_pix", "Receita de vendas (pix)", CategoryType.INCOME, DreClassification.GROSS_REVENUE, "qr_code"),
        category("cat_sales_cash", "Receita de vendas (dinheiro)", CategoryType.INCOME, DreClassification.GROSS_REVENUE, "payments"),
        category("cat_sales_receipt", "Receitas de vendas (notinha)", CategoryType.INCOME, DreClassification.GROSS_REVENUE, "receipt"),
        // Deduções
        category("cat_fees", "Taxas", CategoryType.EXPENSE, DreClassification.DEDUCTION, "percent"),
        category("cat_taxes", "Impostos", CategoryType.EXPENSE, DreClassification.DEDUCTION, "payments"),
        // CMV
        category("cat_purchases", "Compras", CategoryType.EXPENSE, DreClassification.COST, "shopping_cart"),
        // Despesas Operacionais — Fixas
        category("cat_rent", "Aluguel", CategoryType.EXPENSE, DreClassification.EXPENSE, "home"),
        category("cat_electricity", "Luz", CategoryType.EXPENSE, DreClassification.EXPENSE, "bolt"),
        category("cat_water", "Água", CategoryType.EXPENSE, DreClassification.EXPENSE, "water_drop"),
        category("cat_phone", "Telefone", CategoryType.EXPENSE, DreClassification.EXPENSE, "phone"),
        category("cat_water_drinking", "Água de beber", CategoryType.EXPENSE, DreClassification.EXPENSE, "local_drink"),
        // Despesas Operacionais — Variáveis
        category("cat_transport", "Transporte", CategoryType.EXPENSE, DreClassification.EXPENSE, "directions_car"),
        category("cat_packaging", "Embalagens", CategoryType.EXPENSE, DreClassification.EXPENSE, "inventory_2"),
        // Despesas Financeiras
        category("cat_loans", "Empréstimos", CategoryType.EXPENSE, DreClassification.FINANCIAL_EXPENSE, "account_balance"),
        category("cat_overdraft", "Juros de cheque especial", CategoryType.EXPENSE, DreClassification.FINANCIAL_EXPENSE, "trending_down"),
        // Transferência
        category("cat_transfer", "Transferência", CategoryType.BOTH, DreClassification.NONE, "swap_horiz"),
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
