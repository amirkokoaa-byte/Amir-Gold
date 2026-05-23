package com.example.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface MarketApiService {
    @GET
    suspend fun getExchangeRates(@Url url: String): CurrencyResponse
    
    // Example using metalpriceapi.com or similar
    @GET
    suspend fun getGoldRates(
        @Url url: String,
        @Query("api_key") apiKey: String,
        @Query("base") base: String = "USD",
        @Query("currencies") currencies: String = "XAU,XAG"
    ): GoldMarketResponse

    @GET
    suspend fun getBinancePrice(@Url url: String): BinancePriceResponse
}
