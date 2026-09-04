package com.example.thermowear.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.example.thermowear.R
import com.example.thermowear.model.ThermoWearAlert
import com.example.thermowear.ui.alerts.AlertsActivity

object NotificationHelper {

    private const val CHANNEL_ID =
        "thermowear_alerts"

    private const val CHANNEL_NAME =
        "Alertas ThermoWear"

    private const val CHANNEL_DESCRIPTION =
        "Alertas importantes de los dispositivos ThermoWear"


    fun createNotificationChannel(
        context: Context
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val notificationManager =
                context.getSystemService(
                    NotificationManager::class.java
                )


            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {

                    description =
                        CHANNEL_DESCRIPTION
                }


            notificationManager
                .createNotificationChannel(
                    channel
                )
        }
    }


    fun showAlertNotification(
        context: Context,
        alert: ThermoWearAlert
    ) {

        createNotificationChannel(
            context
        )


        /*
         * Android 13 o superior.
         *
         * Si el usuario todavía no concedió
         * permiso, simplemente no intentamos
         * mostrar la notificación.
         *
         * La alerta interna de ThermoWear
         * sí seguirá almacenada.
         */
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }


        val intent =
            Intent(
                context,
                AlertsActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }


        val pendingIntent =
            PendingIntent.getActivity(

                context,

                alert.id.hashCode(),

                intent,

                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )


        val notification =
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O
            ) {

                Notification.Builder(
                    context,
                    CHANNEL_ID
                )

            } else {

                Notification.Builder(
                    context
                )
            }
                .setSmallIcon(
                    R.drawable.ic_notifications
                )
                .setContentTitle(
                    alert.title
                )
                .setContentText(
                    alert.message
                )
                .setStyle(
                    Notification.BigTextStyle()
                        .bigText(
                            alert.message
                        )
                )
                .setContentIntent(
                    pendingIntent
                )
                .setAutoCancel(
                    true
                )
                .setCategory(
                    Notification.CATEGORY_STATUS
                )
                .setWhen(
                    alert.timestampMillis
                )
                .build()


        val notificationManager =
            context.getSystemService(
                NotificationManager::class.java
            )


        notificationManager.notify(
            alert.id.hashCode(),
            notification
        )
    }
}