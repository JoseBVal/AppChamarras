package com.example.thermowear.model

enum class ThermoWearVersion {
    V1,
    V2
}

data class ThermoWearDevice(
    val id: String,
    val name: String,
    val version: ThermoWearVersion,
    val battery: Int,
    val connected: Boolean
) {

    val zoneCount: Int
        get() = when (version) {
            ThermoWearVersion.V1 -> 1
            ThermoWearVersion.V2 -> 2
        }

    val hasTemperatureSensor: Boolean
        get() = version == ThermoWearVersion.V2
}