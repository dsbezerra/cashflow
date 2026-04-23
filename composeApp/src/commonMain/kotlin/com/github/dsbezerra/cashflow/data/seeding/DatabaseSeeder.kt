package com.github.dsbezerra.cashflow.data.seeding

import com.github.dsbezerra.cashflow.db.Category
import com.github.dsbezerra.cashflow.db.CategoryQueries
import com.github.dsbezerra.cashflow.domain.model.CategoryType
import com.github.dsbezerra.cashflow.domain.model.DreClassification

class DatabaseSeeder(private val categoryQueries: CategoryQueries) {

    fun seedIfEmpty() {
        if (categoryQueries.selectAll().executeAsList().isNotEmpty()) return
        defaultCategories.forEach { categoryQueries.insert(it) }
    }

    private val defaultCategories = listOf(
        category("cat_salary",        "Salário",            CategoryType.INCOME,  DreClassification.GROSS_REVENUE, "salary",          "#4CAF50"),
        category("cat_freelance",     "Freelance",          CategoryType.INCOME,  DreClassification.GROSS_REVENUE, "work",            "#8BC34A"),
        category("cat_sales",         "Receita de Vendas",  CategoryType.INCOME,  DreClassification.GROSS_REVENUE, "store",           "#66BB6A"),
        category("cat_investment",    "Investimentos",      CategoryType.INCOME,  DreClassification.NONE,          "trending_up",     "#009688"),
        category("cat_other_income",  "Outros rendimentos", CategoryType.INCOME,  DreClassification.GROSS_REVENUE, "attach_money",    "#00BCD4"),
        category("cat_taxes",         "Impostos",           CategoryType.EXPENSE, DreClassification.DEDUCTION,     "receipt_long",    "#FF7043"),
        category("cat_returns",       "Devoluções",         CategoryType.EXPENSE, DreClassification.DEDUCTION,     "assignment_return","#FFAB40"),
        category("cat_cogs",          "Custo de Mercadoria",CategoryType.EXPENSE, DreClassification.COST,          "inventory",       "#8D6E63"),
        category("cat_food",          "Alimentação",        CategoryType.EXPENSE, DreClassification.EXPENSE,       "restaurant",      "#F44336"),
        category("cat_transport",     "Transporte",         CategoryType.EXPENSE, DreClassification.EXPENSE,       "directions_car",  "#FF9800"),
        category("cat_housing",       "Moradia",            CategoryType.EXPENSE, DreClassification.EXPENSE,       "home",            "#795548"),
        category("cat_health",        "Saúde",              CategoryType.EXPENSE, DreClassification.EXPENSE,       "local_hospital",  "#E91E63"),
        category("cat_leisure",       "Lazer",              CategoryType.EXPENSE, DreClassification.EXPENSE,       "sports_esports",  "#9C27B0"),
        category("cat_education",     "Educação",           CategoryType.EXPENSE, DreClassification.EXPENSE,       "school",          "#3F51B5"),
        category("cat_clothing",      "Vestuário",          CategoryType.EXPENSE, DreClassification.EXPENSE,       "checkroom",       "#607D8B"),
        category("cat_subscriptions", "Assinaturas",        CategoryType.EXPENSE, DreClassification.EXPENSE,       "subscriptions",   "#FF5722"),
        category("cat_other_expense", "Outros gastos",      CategoryType.EXPENSE, DreClassification.EXPENSE,       "more_horiz",      "#9E9E9E"),
        category("cat_transfer",      "Transferência",      CategoryType.BOTH,    DreClassification.NONE,          "swap_horiz",      "#78909C"),
    )

    private fun category(
        id: String,
        name: String,
        type: CategoryType,
        dreClassification: DreClassification,
        icon: String,
        color: String,
    ) = Category(
        id = id,
        name = name,
        type = type,
        dreClassification = dreClassification,
        icon = icon,
        color = color,
        parentId = null,
        isDefault = true,
        isArchived = false,
    )
}
