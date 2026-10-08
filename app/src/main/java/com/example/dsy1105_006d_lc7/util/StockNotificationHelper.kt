package com.example.dsy1105_006d_lc7.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.dsy1105_006d_lc7.MainActivity

/**
 * Gestor de notificaciones locales para alertas de stock crítico y reposición.
 */
object StockNotificationHelper {

    private const val CHANNEL_ID = "stock_alerts_channel"
    private const val CHANNEL_NAME = "Alertas de Stock Automotriz"
    private const val CHANNEL_DESC = "Notificaciones cuando un repuesto o insumo alcanza el stock mínimo o se agota"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showCriticalStockAlert(
        context: Context,
        productId: Long,
        productName: String,
        currentStock: Int,
        minStock: Int
    ) {
        createNotificationChannel(context)

        // Verificar permiso en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            productId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (currentStock == 0) {
            "🔴 ¡Stock Agotado!: $productName"
        } else {
            "⚠️ Stock Crítico: $productName"
        }

        val text = if (currentStock == 0) {
            "El producto no tiene existencias. Reposición urgente recomendada."
        } else {
            "Quedan solo $currentStock unidades (Mínimo recomendado: $minStock)."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$text\nIngresa al módulo de reposición para solicitar pedido a proveedores."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(productId.toInt(), notification)
        } catch (_: SecurityException) {
            // Silencioso si no hay permisos concedidos
        }
    }
}
