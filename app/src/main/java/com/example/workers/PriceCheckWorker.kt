package com.example.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.repository.MarketDataState
import com.example.data.repository.MarketRepository
import kotlinx.coroutines.flow.first

class PriceCheckWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val db = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "metal-prices-db"
        ).build()
        val repository = MarketRepository(db.alertDao())

        val alerts = repository.allAlerts.first().filter { it.isEnabled }
        if (alerts.isEmpty()) return Result.success()

        val marketState = repository.getLiveMarketData().first()
        if (marketState is MarketDataState.Success) {
            val prices = marketState.prices
            val pricesMap = mapOf(
                "ذهب عيار 24" to prices.gold24,
                "ذهب عيار 21" to prices.gold21,
                "ذهب عيار 18" to prices.gold18,
                "فضة" to prices.silver,
                "سبيكة ذهب" to prices.goldCoin,
                "دولار أمريكي" to prices.usd,
                "يورو" to prices.eur
            )

            alerts.forEach { alert ->
                val currentPrice = pricesMap[alert.itemName] ?: return@forEach
                if (alert.isUp && currentPrice >= alert.targetPrice) {
                    sendNotification("وصول السعر!", "سعر ${alert.itemName} وصل إلى ${"%.2f".format(currentPrice)} وصعد للهدف المنتظر.")
                    repository.updateAlertStatus(alert.id, false)
                } else if (!alert.isUp && currentPrice <= alert.targetPrice) {
                    sendNotification("هبوط السعر!", "سعر ${alert.itemName} نزل إلى ${"%.2f".format(currentPrice)} ووصل للهدف المنتظر.")
                    repository.updateAlertStatus(alert.id, false)
                }
            }
        }

        return Result.success()
    }

    private fun sendNotification(title: String, content: String) {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "price_alerts_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "تنبيهات الأسعار", NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
