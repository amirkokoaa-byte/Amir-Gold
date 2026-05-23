package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.Alert
import com.example.data.local.AlertDao
import com.example.data.remote.MarketApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MarketRepository(private val alertDao: AlertDao) {
    private val apiService: MarketApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummy.com/") // We use @Url in methods to handle varying bases
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(MarketApiService::class.java)
    }

    private val GOLD_API_KEY = runCatching<String> { BuildConfig.MARKET_API_KEY }.getOrDefault("")

    val allAlerts: Flow<List<Alert>> = alertDao.getAllAlerts()

    suspend fun insertAlert(alert: Alert) = alertDao.insertAlert(alert)
    suspend fun deleteAlert(id: Int) = alertDao.deleteAlertById(id)
    suspend fun updateAlertStatus(id: Int, isEnabled: Boolean) = alertDao.updateAlertStatus(id, isEnabled)

    // Flow that emits new data periodically
    fun getLiveMarketData(): Flow<MarketDataState> = flow {
        while (true) {
            emit(fetchMarketData())
            delay(60_000) // update every minute
        }
    }

    private suspend fun fetchMarketData(): MarketDataState {
        return try {
            // 1. Fetch Real Currencies (Free API)
            val currResponse = apiService.getExchangeRates("https://api.exchangerate-api.com/v4/latest/USD")
            val usdToEgp = currResponse.rates["EGP"] ?: 48.0
            val eurToEgp = (currResponse.rates["EGP"] ?: 48.0) / (currResponse.rates["EUR"] ?: 1.0)
            val gbpToEgp = (currResponse.rates["EGP"] ?: 48.0) / (currResponse.rates["GBP"] ?: 1.0)
            val sarToEgp = (currResponse.rates["EGP"] ?: 48.0) / (currResponse.rates["SAR"] ?: 1.0)
            val aedToEgp = (currResponse.rates["EGP"] ?: 48.0) / (currResponse.rates["AED"] ?: 1.0)

            // 2. Fetch Gold
            var globalGoldUsdPerOz = 2300.0
            var globalSilverUsdPerOz = 28.0
            var isGoldReal = false
            
            if (GOLD_API_KEY.isNotBlank() && GOLD_API_KEY != "MY_GEMINI_API_KEY" && GOLD_API_KEY != "MY_MARKET_API_KEY") {
                try {
                    val goldRes = apiService.getGoldRates(
                        "https://api.metalpriceapi.com/v1/latest", 
                        GOLD_API_KEY
                    )
                    globalGoldUsdPerOz = 1.0 / (goldRes.rates["XAU"] ?: (1.0 / 2300.0))
                    globalSilverUsdPerOz = 1.0 / (goldRes.rates["XAG"] ?: (1.0 / 28.0))
                    isGoldReal = true
                } catch (e: Exception) {
                    // Fallback on error so the app doesn't crash, but flag it
                    e.printStackTrace()
                }
            } else {
                // Use Binance PAXG API (free, no auth) to get real-time gold price (PAXG is pegged 1:1 to Gold Ounce)
                try {
                    val binanceRes = apiService.getBinancePrice("https://api.binance.com/api/v3/ticker/price?symbol=PAXGUSDT")
                    globalGoldUsdPerOz = binanceRes.price.toDoubleOrNull() ?: 2300.0
                    // Estimate silver based on gold/silver ratio of ~82
                    globalSilverUsdPerOz = globalGoldUsdPerOz / 82.0
                    isGoldReal = true
                } catch (e: Exception) {
                    // Fallback to simulated prices if no network
                    globalGoldUsdPerOz = 2300.0 + (System.currentTimeMillis() % 10000) / 1000.0
                    globalSilverUsdPerOz = 28.0 + (System.currentTimeMillis() % 10000) / 5000.0
                }
            }

            // Calculate EGP per gram
            val gramPerOz = 31.1034768
            val usdPerGram24 = globalGoldUsdPerOz / gramPerOz
            val silverUsdPerGram = globalSilverUsdPerOz / gramPerOz
            
            // To simulate local market premium (which often exists in Egypt), we add a small markup
            val marketPremium = 1.01 
            val egpPerGram24 = usdPerGram24 * usdToEgp * marketPremium

            val prices = MarketPrices(
                gold24 = egpPerGram24,
                gold21 = egpPerGram24 * (21.0 / 24.0),
                gold18 = egpPerGram24 * (18.0 / 24.0),
                silver = silverUsdPerGram * usdToEgp * marketPremium,
                goldCoin = egpPerGram24 * 21.0 / 24.0 * 8.0, // 8 grams of 21k
                usd = usdToEgp,
                eur = eurToEgp,
                gbp = gbpToEgp,
                sar = sarToEgp,
                aed = aedToEgp,
                isGoldReal = isGoldReal
            )
            MarketDataState.Success(prices)
        } catch (e: Exception) {
            MarketDataState.Error(e.message ?: "Unknown Error")
        }
    }
}

data class MarketPrices(
    val gold24: Double,
    val gold21: Double,
    val gold18: Double,
    val silver: Double,
    // bullion - 8grams of 21k
    val goldCoin: Double, 
    val usd: Double,
    val eur: Double,
    val gbp: Double,
    val sar: Double,
    val aed: Double,
    val isGoldReal: Boolean
)

sealed class MarketDataState {
    object Loading : MarketDataState()
    data class Success(val prices: MarketPrices) : MarketDataState()
    data class Error(val message: String) : MarketDataState()
}
