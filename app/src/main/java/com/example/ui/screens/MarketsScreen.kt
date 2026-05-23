package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.MarketDataState
import com.example.data.repository.MarketPrices
import com.example.ui.components.NeonCard
import com.example.ui.components.PriceRow

@Composable
fun MarketsScreen(marketState: MarketDataState) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (marketState) {
            is MarketDataState.Loading -> {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                        if (!prices.isGoldReal) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                                    Spacer(Modifier.width(8.dp))
                                    Text("أضف مفتاح API في الإعدادات لعرض الأسعار الحقيقية للذهب. يتم عرض أسعار تقديرية.", color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                            }
                        }
                    }

                    item {
                        Text("أسعار الذهب (جرام)", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                        NeonCard(neonColor = MaterialTheme.colorScheme.primary) {
                            PriceRow(label = "عيار 24", price = prices.gold24)
                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            PriceRow(label = "عيار 21", price = prices.gold21)
                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            PriceRow(label = "عيار 18", price = prices.gold18)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        Text("أسعار الفضة (جرام)", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                        NeonCard(neonColor = MaterialTheme.colorScheme.secondary) {
                            PriceRow(label = "الفضة النقية", price = prices.silver)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        Text("السبائك والعملات الدهبية", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                        NeonCard(neonColor = MaterialTheme.colorScheme.tertiary) {
                            PriceRow(label = "جنيه ذهب (٨ جرام عيار ٢١)", price = prices.goldCoin)
                        }
                    }
                }
            }
        }
    }
}
