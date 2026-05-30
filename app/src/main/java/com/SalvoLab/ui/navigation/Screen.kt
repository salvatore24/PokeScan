package com.SalvoLab.ui.navigation

sealed class Screen(val route: String) {
    // Auth
    data object Login    : Screen("login")
    data object Register : Screen("register")

    // Main tabs
    data object Home    : Screen("home")
    data object Market  : Screen("market")
    data object Scan    : Screen("scan")
    data object Profile : Screen("profile")

    // Detail (con argomento)
    data object CardDetail : Screen("card_detail/{cardId}") {
        fun createRoute(cardId: String) = "card_detail/$cardId"
    }
    data object InventoryItemDetail : Screen("inventory_item/{itemId}") {
        fun createRoute(itemId: String) = "inventory_item/$itemId"
    }
}
