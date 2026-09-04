package com.example.thermowear.data

import android.content.Context

object SettingsManager {

    private const val PREFS_NAME = "thermowear_settings"

    private const val KEY_NOTIFICATIONS = "notifications_enabled"
    private const val KEY_SOUND = "sound_enabled"
    private const val KEY_VIBRATION = "vibration_enabled"


    fun areNotificationsEnabled(context: Context): Boolean {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return preferences.getBoolean(
            KEY_NOTIFICATIONS,
            true
        )
    }


    fun setNotificationsEnabled(
        context: Context,
        enabled: Boolean
    ) {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putBoolean(
                KEY_NOTIFICATIONS,
                enabled
            )
            .apply()
    }


    fun isSoundEnabled(context: Context): Boolean {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return preferences.getBoolean(
            KEY_SOUND,
            true
        )
    }


    fun setSoundEnabled(
        context: Context,
        enabled: Boolean
    ) {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putBoolean(
                KEY_SOUND,
                enabled
            )
            .apply()
    }


    fun isVibrationEnabled(context: Context): Boolean {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return preferences.getBoolean(
            KEY_VIBRATION,
            true
        )
    }


    fun setVibrationEnabled(
        context: Context,
        enabled: Boolean
    ) {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putBoolean(
                KEY_VIBRATION,
                enabled
            )
            .apply()
    }
}