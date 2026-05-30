@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.SalvoLab.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.SalvoLab.ui.auth.AuthEvent
import com.SalvoLab.ui.auth.AuthViewModel
import com.SalvoLab.ui.carddetail.CardDetailScreen
import com.SalvoLab.ui.market.MarketScreen
import com.SalvoLab.ui.navigation.Screen
import com.SalvoLab.ui.navigation.bottomNavItems
import com.SalvoLab.ui.profile.ProfileScreen

/**
 * Scaffold principale con NavigationBar Material 3.
 * NavHost interno gestisce Home, Market, Scan, Profile + CardDetail.
 */
@Composable
fun MainScaffold(
    onSignOut: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val innerNavController = rememberNavController()
    val navBackStackEntry  by innerNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Nasconde la bottom bar nelle schermate di dettaglio
    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                            onClick  = {
                                innerNavController.navigate(item.screen.route) {
                                    popUpTo(innerNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon  = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = innerNavController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Home.route) {
                // Tab "Inventario" = collezione personale
                com.SalvoLab.ui.inventory.InventoryScreen(
                    onCardClick = { cardId -> innerNavController.navigate(Screen.CardDetail.createRoute(cardId)) },
                )
            }

            composable(Screen.Market.route) {
                MarketScreen(
                    onCardClick = { cardId -> innerNavController.navigate(Screen.CardDetail.createRoute(cardId)) },
                )
            }

            composable(Screen.Scan.route) {
                ScanPlaceholder()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(onSignOut = {
                    authViewModel.onEvent(AuthEvent.ErrorDismissed)
                    onSignOut()
                })
            }

            composable(
                route     = Screen.CardDetail.route,
                arguments = listOf(navArgument("cardId") { type = NavType.StringType }),
            ) {
                CardDetailScreen(onNavigateBack = { innerNavController.popBackStack() })
            }
        }
    }
}

@Composable
fun ScanPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "Scanner Carte\n(CameraX – prossima feature)",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
