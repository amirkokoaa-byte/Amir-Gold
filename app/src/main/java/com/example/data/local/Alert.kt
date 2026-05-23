package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemName: String,
    val targetPrice: Double,
    val isUp: Boolean, // True if we alert when going ABOVE this price, False if BELOW
    val isEnabled: Boolean = true
)
