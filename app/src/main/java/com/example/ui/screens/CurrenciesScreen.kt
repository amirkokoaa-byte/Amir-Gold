package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.MarketDataState
import com.example.ui.components.NeonCard
import com.example.ui.components.PriceRow
import com.example.ui.theme.NeonCyan

@Composable
fun CurrenciesScreen(marketState: MarketDataState) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (marketState) {
            is MarketDataState.Loading -> {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
            }
            is MarketDataState.Error -> {
                Text("حدث خطأ: ${marketState.message}", color = MaterialTheme.colorScheme.error)
            }
            is MarketDataState.Success -> {
                val prices = marketState.prices
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            text = "أسعار العملات مقابل الجنيه المصري",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        NeonCard(neonColor = NeonCyan) {
                            PriceRow(label = "الدولار الأمريكي (USD)", price = prices.usd)
                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            PriceRow(label = "اليورو (EUR)", price = prices.eur)
                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            PriceRow(label = "الجنيه الإسترليني (GBP)", price = prices.gbp)
                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            PriceRow(label = "الريال السعودي (SAR)", price = prices.sar)
                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            PriceRow(label = "الدرهم الإماراتي (AED)", price = prices.aed)
                        }
                    }
                }
            }
        }
    }
}
