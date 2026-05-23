package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.Alert
import com.example.ui.components.NeonCard
import com.example.ui.theme.NeonRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    alerts: List<Alert>,
    onAddAlert: (String, Double, Boolean) -> Unit,
    onDeleteAlert: (Int) -> Unit,
    onToggleAlert: (Int, Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    "تنبيهات الأسعار",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (alerts.isEmpty()) {
                item {
                    Text("لا توجد تنبيهات حالياً.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(alerts) { alert ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(alert.itemName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(
                                    text = if (alert.isUp) "عند الصعود فوق ${alert.targetPrice}" else "عند الهبوط تحت ${alert.targetPrice}",
                                    color = if (alert.isUp) MaterialTheme.colorScheme.primary else NeonRed
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = alert.isEnabled,
                                    onCheckedChange = { onToggleAlert(alert.id, it) }
                                )
                                IconButton(onClick = { onDeleteAlert(alert.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Alert", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(Icons.Default.Add, contentDescription = "إضافة تنبيه")
        }

        if (showDialog) {
            AddAlertDialog(
                onDismiss = { showDialog = false },
                onAdd = { name, price, isUp ->
                    onAddAlert(name, price, isUp)
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertDialog(onDismiss: () -> Unit, onAdd: (String, Double, Boolean) -> Unit) {
    var selectedItem by remember { mutableStateOf("ذهب عيار 21") }
    var targetPrice by remember { mutableStateOf("") }
    var isUp by remember { mutableStateOf(true) }

    val items = listOf("ذهب عيار 24", "ذهب عيار 21", "ذهب عيار 18", "فضة", "دولار أمريكي")

    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة تنبيه جديد") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedItem,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("العنصر") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        items.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    selectedItem = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = targetPrice,
                    onValueChange = { targetPrice = it },
                    label = { Text("السعر المستهدف") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("نوع التنبيه: ")
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = isUp,
                        onClick = { isUp = true },
                        label = { Text("صعود") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = !isUp,
                        onClick = { isUp = false },
                        label = { Text("هبوط") }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val price = targetPrice.toDoubleOrNull()
                if (price != null) {
                    onAdd(selectedItem, price, isUp)
                }
            }) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
