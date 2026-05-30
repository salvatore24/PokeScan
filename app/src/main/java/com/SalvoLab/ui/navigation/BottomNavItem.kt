package com.SalvoLab.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Store
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home,    "Inventario", Icons.Default.GridView),
    BottomNavItem(Screen.Market,  "Mercato",    Icons.Default.Store),
    BottomNavItem(Screen.Scan,    "Scansiona",  Icons.Default.QrCodeScanner),
    BottomNavItem(Screen.Profile, "Profilo",    Icons.Default.Person),
)
