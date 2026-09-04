package com.example.thermowear.model


enum class ThermoWearZone(
    val displayName: String
) {

    // ThermoWear V1
    BACK("Espalda"),

    // ThermoWear V2
    UPPER_BACK("Espalda alta"),

    LOWER_BACK("Espalda baja")
}


/**
 * Periodo durante el cual una zona estuvo ACTIVADA
 * con una intensidad determinada.
 *
 * IMPORTANTE:
 *
 * Esto representa el tiempo lógico para el usuario.
 *
 * En ThermoWear V2, aunque Arduino alterne físicamente
 * la alimentación entre ambas zonas, si el usuario
 * mantiene las dos encendidas durante 20 minutos,
 * cada zona registra 20 minutos de uso lógico.
 */
data class IntensityPeriod(

    /**
     * 1 = Bajo
     * 2 = Medio
     * 3 = Alto
     */
    val intensity: Int,

    val startTimeMillis: Long,

    val endTimeMillis: Long

) {

    val durationMillis: Long
        get() = (
                endTimeMillis - startTimeMillis
                ).coerceAtLeast(0L)


    val intensityLabel: String
        get() = when (intensity) {

            1 -> "Bajo"

            2 -> "Medio"

            3 -> "Alto"

            else -> "Sin definir"
        }
}


/**
 * Uso de una zona durante una sesión.
 *
 * Una zona puede tener varios periodos porque
 * el usuario puede cambiar la intensidad mientras
 * la chamarra está funcionando.
 */
data class HistoryZoneUsage(

    val zone: ThermoWearZone,

    val intensityPeriods: List<IntensityPeriod>,

    /**
     * Campo preparado para ThermoWear V2.
     *
     * Solo tendría sentido para Espalda alta,
     * que es la zona que tendrá sensor.
     *
     * Por ahora no se muestra en Historial.
     */
    val averageTemperatureC: Double? = null

) {

    /**
     * Tiempo lógico que la zona estuvo encendida.
     *
     * Se calcula sumando todos sus periodos.
     */
    val activeDurationMillis: Long
        get() = intensityPeriods.sumOf {
            it.durationMillis
        }
}


/**
 * Sesión completa de utilización de una chamarra.
 */
data class HistorySession(

    val id: String,

    val deviceId: String,

    val version: ThermoWearVersion,

    val startTimeMillis: Long,

    val endTimeMillis: Long,

    val zones: List<HistoryZoneUsage>

) {

    /**
     * Tiempo transcurrido desde que comienza
     * hasta que termina la sesión.
     *
     * No se obtiene sumando las zonas.
     */
    val durationMillis: Long
        get() = (
                endTimeMillis - startTimeMillis
                ).coerceAtLeast(0L)
}