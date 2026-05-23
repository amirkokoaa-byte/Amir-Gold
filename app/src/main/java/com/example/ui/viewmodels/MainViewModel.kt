package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.local.Alert
import com.example.data.local.AppDatabase
import com.example.data.repository.MarketDataState
import com.example.data.repository.MarketRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "metal-prices-db"
    ).build()

    private val repository = MarketRepository(db.alertDao())

    val marketState: StateFlow<MarketDataState> = repository.getLiveMarketData()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MarketDataState.Loading
        )

    val alerts: StateFlow<List<Alert>> = repository.allAlerts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addAlert(itemName: String, targetPrice: Double, isUp: Boolean) {
        viewModelScope.launch {
            repository.insertAlert(Alert(itemName = itemName, targetPrice = targetPrice, isUp = isUp))
        }
    }

    fun removeAlert(id: Int) {
        viewModelScope.launch {
            repository.deleteAlert(id)
        }
    }

    fun toggleAlert(id: Int, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateAlertStatus(id, isEnabled)
        }
    }
}
