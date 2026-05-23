package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.MarketDataState
import com.example.ui.components.NeonCard
import com.example.ui.theme.NeonGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(marketState: MarketDataState) {
    var weightText by remember { mutableStateOf("") }
    var masnaweyaText by remember { mutableStateOf("") }
    var selectedKarat by remember { mutableStateOf(21) }

    val prices = (marketState as? MarketDataState.Success)?.prices

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(
                "الحاسبة الذكية للذهب",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("الوزن (جرام)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = masnaweyaText,
                onValueChange = { masnaweyaText = it },
                label = { Text("المصنعية للجرام (ج.م)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            Text("العيار:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf(24, 21, 18).forEach { karat ->
                    FilterChip(
                        selected = selectedKarat == karat,
                        onClick = { selectedKarat = karat },
                        label = { Text("عيار $karat") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (prices != null) {
                val pricePerGram = when (selectedKarat) {
                    24 -> prices.gold24
                    21 -> prices.gold21
                    18 -> prices.gold18
                    else -> 0.0
                }

                val weight = weightText.toDoubleOrNull() ?: 0.0
                val masnaweya = masnaweyaText.toDoubleOrNull() ?: 0.0

                val totalGoldValue = pricePerGram * weight
                val totalMasnaweyaValue = masnaweya * weight
                val finalTotal = totalGoldValue + totalMasnaweyaValue

                NeonCard(neonColor = NeonGreen) {
                    Text("النتائج", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("قيمة الذهب الصافي:")
                        Text(String.format("%.2f ج.م", totalGoldValue))
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("إجمالي المصنعية:")
                        Text(String.format("%.2f ج.م", totalMasnaweyaValue))
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الإجمالي المطلوب:", fontWeight = FontWeight.Bold)
                        Text(String.format("%.2f ج.م", finalTotal), fontWeight = FontWeight.Bold, color = NeonGreen)
                    }
                }
            } else {
                Text("جاري تحميل الأسعار...", modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}
