package com.github.dsbezerra.cashflow.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

fun categoryIcon(iconName: String): ImageVector = when (iconName.lowercase()) {
    "home" -> Icons.Default.Home
    "work" -> Icons.Default.Work
    "attach_money", "money" -> Icons.Default.AttachMoney
    "trending_up", "trending" -> Icons.Default.TrendingUp
    "swap_horiz", "transfer" -> Icons.Default.SwapHoriz
    "payments" -> Icons.Default.Payments
    "restaurant", "food" -> Icons.Default.Restaurant
    "directions_car", "car", "transport" -> Icons.Default.DirectionsCar
    "school", "education" -> Icons.Default.School
    "shopping_cart", "shopping" -> Icons.Default.ShoppingCart
    "local_hospital", "health" -> Icons.Default.LocalHospital
    "favorite" -> Icons.Default.Favorite
    "movie", "entertainment" -> Icons.Default.Movie
    "flight", "travel" -> Icons.Default.Flight
    "sports_esports", "games" -> Icons.Default.SportsEsports
    else -> Icons.Default.Category
}

val categoryIconOptions = listOf(
    "work",
    "attach_money",
    "trending_up",
    "payments",
    "restaurant",
    "home",
    "directions_car",
    "school",
    "shopping_cart",
    "local_hospital",
    "favorite",
    "movie",
    "flight",
    "sports_esports",
    "swap_horiz",
)
