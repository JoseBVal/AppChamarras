package com.example.thermowear.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import com.example.thermowear.R
import com.example.thermowear.data.SettingsManager
import com.example.thermowear.model.ThermoWearAlert
import com.example.thermowear.ui.alerts.AlertsActivity

object NotificationHelper {

    private const val CHANNEL_NAME =
        "Alertas ThermoWear"

    private const val CHANNEL_DESCRIPTION =
        "Alertas importantes de ThermoWear"


    /*
     * Se utilizan distintos canales porque
     * Android no permite modificar sonido
     * o vibración de un canal después
     * de haber sido creado.
     */

    private const val CHANNEL_SOUND_VIBRATION =
        "thermowear_alerts_sound_vibration"

    private const val CHANNEL_SOUND_ONLY =
        "thermowear_alerts_sound"

    private const val CHANNEL_VIBRATION_ONLY =
        "thermowear_alerts_vibration"

    private const val CHANNEL_SILENT =
        "thermowear_alerts_silent"


    fun cancelAlertNotifications(context: Context) {
        val manager =
            context.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.activeNotifications
                .filter { statusBarNotification ->
                    statusBarNotification.notification.channelId
                        ?.startsWith("thermowear_alerts_") == true
                }
                .forEach { statusBarNotification ->
                    manager.cancel(statusBarNotification.tag, statusBarNotification.id)
                }
        } else {
            manager.cancelAll()
        }
    }


    fun createNotificationChannel(
        context: Context
    ) {

        if (
            Build.VERSION.SDK_INT <
            Build.VERSION_CODES.O
        ) {
            return
        }


        val soundEnabled =
            SettingsManager.isSoundEnabled(
                context
            )

        val vibrationEnabled =
            SettingsManager.isVibrationEnabled(
                context
            )


        val channelId =
            getChannelId(
                soundEnabled,
                vibrationEnabled
            )


        val notificationManager =
            context.getSystemService(
                NotificationManager::class.java
            )


        val channel =
            NotificationChannel(
                channelId,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description =
                    CHANNEL_DESCRIPTION


                /*
                 * SONIDO
                 */

                if (soundEnabled) {

                    val audioAttributes =
                        AudioAttributes.Builder()
                            .setUsage(
                                AudioAttributes.USAGE_NOTIFICATION
                            )
                            .build()

                    setSound(
                        Settings.System.DEFAULT_NOTIFICATION_URI,
                        audioAttributes
                    )

                } else {

                    setSound(
                        null,
                        null
                    )
                }


                /*
                 * VIBRACIÓN
                 */

                enableVibration(
                    vibrationEnabled
                )

                if (vibrationEnabled) {

                    vibrationPattern =
                        longArrayOf(
                            0,
                            250,
                            150,
                            250
                        )
                }
            }


        notificationManager
            .createNotificationChannel(
                channel
            )
    }


    fun showAlertNotification(
        context: Context,
        alert: ThermoWearAlert
    ) {

        /*
         * Si el usuario desactivó
         * las notificaciones internas
         * de ThermoWear, no se muestra
         * la notificación Android.
         *
         * AlertManager seguirá guardando
         * la alerta normalmente.
         */

        if (
            !SettingsManager
                .areNotificationsEnabled(
                    context
                )
        ) {

            return
        }


        /*
         * Android 13 o superior necesita
         * POST_NOTIFICATIONS.
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


        val soundEnabled =
            SettingsManager.isSoundEnabled(
                context
            )

        val vibrationEnabled =
            SettingsManager.isVibrationEnabled(
                context
            )


        val channelId =
            getChannelId(
                soundEnabled,
                vibrationEnabled
            )


        createNotificationChannel(
            context
        )


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


        val builder =
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O
            ) {

                Notification.Builder(
                    context,
                    channelId
                )

            } else {

                Notification.Builder(
                    context
                )
            }


        builder
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


        /*
         * En Android 7.1 o inferior
         * todavía se pueden controlar
         * sonido y vibración desde
         * Notification.Builder.
         */

        if (
            Build.VERSION.SDK_INT <
            Build.VERSION_CODES.O
        ) {

            if (soundEnabled) {

                builder.setSound(
                    Settings.System.DEFAULT_NOTIFICATION_URI
                )

            } else {

                builder.setSound(
                    null
                )
            }


            if (vibrationEnabled) {

                builder.setVibrate(
                    longArrayOf(
                        0,
                        250,
                        150,
                        250
                    )
                )

            } else {

                builder.setVibrate(
                    longArrayOf(0)
                )
            }
        }


        val notification =
            builder.build()


        val notificationManager =
            context.getSystemService(
                NotificationManager::class.java
            )


        notificationManager.notify(
            alert.id.hashCode(),
            notification
        )
    }


    private fun getChannelId(
        soundEnabled: Boolean,
        vibrationEnabled: Boolean
    ): String {

        return when {

            soundEnabled &&
                    vibrationEnabled -> {

                CHANNEL_SOUND_VIBRATION
            }


            soundEnabled &&
                    !vibrationEnabled -> {

                CHANNEL_SOUND_ONLY
            }


            !soundEnabled &&
                    vibrationEnabled -> {

                CHANNEL_VIBRATION_ONLY
            }


            else -> {

                CHANNEL_SILENT
            }
        }
    }
}
