package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class Screen(val route: String, val title: String, val icon: @Composable () -> Unit) {
    object Markets : Screen("markets", "الأسواق", { Icon(Icons.Default.MonetizationOn, contentDescription = null) })
    object Currencies : Screen("currencies", "العملات", { Icon(Icons.Default.List, contentDescription = null) })
    object Calculator : Screen("calculator", "الحاسبة", { Icon(Icons.Default.Calculate, contentDescription = null) })
    object Alerts : Screen("alerts", "التنبيهات", { Icon(Icons.Default.Notifications, contentDescription = null) })
}

val bottomNavItems = listOf(
    Screen.Markets,
    Screen.Currencies,
    Screen.Calculator,
    Screen.Alerts
)

@Composable
fun AppBottomNavigation(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = screen.icon,
                label = { Text(screen.title) }
            )
        }
    }
}
