package com.example.data.remote

data class GoldMarketResponse(
    val base: String,
    val rates: Map<String, Double>
)

data class CurrencyResponse(
    val base: String,
    val rates: Map<String, Double>
)

data class BinancePriceResponse(
    val symbol: String,
    val price: String
)
