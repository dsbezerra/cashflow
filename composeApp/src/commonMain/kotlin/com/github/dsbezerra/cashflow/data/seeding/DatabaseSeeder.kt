package com.github.dsbezerra.cashflow.data.seeding

import com.github.dsbezerra.cashflow.db.Category
import com.github.dsbezerra.cashflow.db.CategoryQueries
import com.github.dsbezerra.cashflow.domain.model.CategoryType

class DatabaseSeeder(private val categoryQueries: CategoryQueries) {

    fun seedIfEmpty() {
        if (categoryQueries.selectAll().executeAsList().isNotEmpty()) return
        defaultCategories.forEach { categoryQueries.insert(it) }
    }

    private val defaultCategories = listOf(
        category("cat_salary",        "Salário",            CategoryType.INCOME,  "salary",          "#4CAF50"),
        category("cat_freelance",     "Freelance",          CategoryType.INCOME,  "work",            "#8BC34A"),
        category("cat_investment",    "Investimentos",      CategoryType.INCOME,  "trending_up",     "#009688"),
        category("cat_other_income",  "Outros rendimentos", CategoryType.INCOME,  "attach_money",    "#00BCD4"),
        category("cat_food",          "Alimentação",        CategoryType.EXPENSE, "restaurant",      "#F44336"),
        category("cat_transport",     "Transporte",         CategoryType.EXPENSE, "directions_car",  "#FF9800"),
        category("cat_housing",       "Moradia",            CategoryType.EXPENSE, "home",            "#795548"),
        category("cat_health",        "Saúde",              CategoryType.EXPENSE, "local_hospital",  "#E91E63"),
        category("cat_leisure",       "Lazer",              CategoryType.EXPENSE, "sports_esports",  "#9C27B0"),
        category("cat_education",     "Educação",           CategoryType.EXPENSE, "school",          "#3F51B5"),
        category("cat_clothing",      "Vestuário",          CategoryType.EXPENSE, "checkroom",       "#607D8B"),
        category("cat_subscriptions", "Assinaturas",        CategoryType.EXPENSE, "subscriptions",   "#FF5722"),
        category("cat_other_expense", "Outros gastos",      CategoryType.EXPENSE, "more_horiz",      "#9E9E9E"),
        category("cat_transfer",      "Transferência",      CategoryType.BOTH,    "swap_horiz",      "#78909C"),
    )

    private fun category(
        id: String,
        name: String,
        type: CategoryType,
        icon: String,
        color: String,
    ) = Category(
        id = id,
        name = name,
        type = type,
        icon = icon,
        color = color,
        parentId = null,
        isDefault = true,
        isArchived = false,
    )
}
