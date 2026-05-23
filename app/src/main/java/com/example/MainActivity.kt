package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ui.navigation.AppBottomNavigation
import com.example.ui.navigation.Screen
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.CurrenciesScreen
import com.example.ui.screens.MarketsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodels.MainViewModel
import com.example.workers.PriceCheckWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule periodic checks for alerts
        val priceCheckRequest = PeriodicWorkRequestBuilder<PriceCheckWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueue(priceCheckRequest)

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val requestPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                val navController = rememberNavController()
                val marketState by viewModel.marketState.collectAsStateWithLifecycle()
                val alerts by viewModel.alerts.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { AppBottomNavigation(navController = navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Markets.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Markets.route) { MarketsScreen(marketState) }
                        composable(Screen.Currencies.route) { CurrenciesScreen(marketState) }
                        composable(Screen.Calculator.route) { CalculatorScreen(marketState) }
                        composable(Screen.Alerts.route) { 
                            AlertsScreen(
                                alerts = alerts,
                                onAddAlert = { name, target, isUp -> viewModel.addAlert(name, target, isUp) },
                                onDeleteAlert = { viewModel.removeAlert(it) },
                                onToggleAlert = { id, enabled -> viewModel.toggleAlert(id, enabled) }
                            ) 
                        }
                    }
                }
            }
        }
    }
}
