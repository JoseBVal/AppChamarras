package com.example.thermowear.model

enum class AlertType {

    DEVICE_DISCONNECTED,

    HIGH_TEMPERATURE
}


data class ThermoWearAlert(

    val id: String,

    val type: AlertType,

    val deviceId: String?,

    val deviceName: String,

    val title: String,

    val message: String,

    val timestampMillis: Long,

    val read: Boolean = false
)