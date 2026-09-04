package com.example.thermowear.data

import android.content.Context
import com.example.thermowear.model.HistorySession
import com.example.thermowear.model.HistoryZoneUsage
import com.example.thermowear.model.IntensityPeriod
import com.example.thermowear.model.ThermoWearDevice
import com.example.thermowear.model.ThermoWearVersion
import com.example.thermowear.model.ThermoWearZone
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import java.util.UUID


object HistoryManager {

    private const val PREFS_NAME =
        "thermowear_history"

    private const val KEY_HISTORY =
        "history_sessions"

    /*
     * Se conserva por compatibilidad
     * con la beta anterior.
     */
    private const val KEY_DEMO_CREATED =
        "demo_history_created"


    /*
     * Versión de los datos de demostración.
     *
     * Esto permite actualizar los datos demo
     * sin pedir al usuario borrar manualmente
     * SharedPreferences.
     */
    private const val KEY_DEMO_VERSION =
        "demo_history_version"

    private const val DEMO_DATA_VERSION =
        2


    /* ============================= */
    /* OBTENER SESIONES              */
    /* ============================= */

    fun getSessions(
        context: Context
    ): MutableList<HistorySession> {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )


        val json =
            preferences.getString(
                KEY_HISTORY,
                null
            ) ?: return mutableListOf()


        val sessions =
            mutableListOf<HistorySession>()


        try {

            val array =
                JSONArray(
                    json
                )


            for (
            i in 0 until array.length()
            ) {

                val item =
                    array.getJSONObject(
                        i
                    )


                val sessionStart =
                    item.getLong(
                        "startTimeMillis"
                    )


                val sessionEnd =
                    item.getLong(
                        "endTimeMillis"
                    )


                val zonesArray =
                    item.getJSONArray(
                        "zones"
                    )


                val zones =
                    mutableListOf<HistoryZoneUsage>()


                for (
                j in 0 until zonesArray.length()
                ) {

                    val zoneItem =
                        zonesArray.getJSONObject(
                            j
                        )


                    val periods =
                        mutableListOf<IntensityPeriod>()


                    /*
                     * FORMATO ACTUAL
                     */
                    if (
                        zoneItem.has(
                            "intensityPeriods"
                        )
                    ) {

                        val periodsArray =
                            zoneItem.getJSONArray(
                                "intensityPeriods"
                            )


                        for (
                        k in 0 until periodsArray.length()
                        ) {

                            val periodItem =
                                periodsArray.getJSONObject(
                                    k
                                )


                            periods.add(

                                IntensityPeriod(

                                    intensity =
                                        periodItem.getInt(
                                            "intensity"
                                        ),

                                    startTimeMillis =
                                        periodItem.getLong(
                                            "startTimeMillis"
                                        ),

                                    endTimeMillis =
                                        periodItem.getLong(
                                            "endTimeMillis"
                                        )
                                )
                            )
                        }

                    } else if (
                        zoneItem.has(
                            "intensity"
                        ) &&
                        zoneItem.has(
                            "activeDurationMillis"
                        )
                    ) {

                        /*
                         * Compatibilidad con el formato
                         * anterior del historial.
                         */

                        val duration =
                            zoneItem.getLong(
                                "activeDurationMillis"
                            )


                        val oldEnd =
                            (
                                    sessionStart +
                                            duration
                                    ).coerceAtMost(
                                    sessionEnd
                                )


                        if (
                            oldEnd >
                            sessionStart
                        ) {

                            periods.add(

                                IntensityPeriod(

                                    intensity =
                                        zoneItem.getInt(
                                            "intensity"
                                        ),

                                    startTimeMillis =
                                        sessionStart,

                                    endTimeMillis =
                                        oldEnd
                                )
                            )
                        }
                    }


                    val temperature =
                        if (
                            zoneItem.has(
                                "averageTemperatureC"
                            ) &&
                            !zoneItem.isNull(
                                "averageTemperatureC"
                            )
                        ) {

                            zoneItem.getDouble(
                                "averageTemperatureC"
                            )

                        } else {

                            null
                        }


                    zones.add(

                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.valueOf(
                                    zoneItem.getString(
                                        "zone"
                                    )
                                ),

                            intensityPeriods =
                                periods,

                            averageTemperatureC =
                                temperature
                        )
                    )
                }


                sessions.add(

                    HistorySession(

                        id =
                            item.getString(
                                "id"
                            ),

                        deviceId =
                            item.getString(
                                "deviceId"
                            ),

                        version =
                            ThermoWearVersion.valueOf(
                                item.getString(
                                    "version"
                                )
                            ),

                        startTimeMillis =
                            sessionStart,

                        endTimeMillis =
                            sessionEnd,

                        zones =
                            zones
                    )
                )
            }

        } catch (_: Exception) {

            return mutableListOf()
        }


        return sessions
            .sortedByDescending {
                it.startTimeMillis
            }
            .toMutableList()
    }


    /* ============================= */
    /* SESIONES POR DISPOSITIVO      */
    /* ============================= */

    fun getSessionsForDevice(
        context: Context,
        deviceId: String
    ): List<HistorySession> {

        return getSessions(
            context
        )
            .filter {

                it.deviceId ==
                        deviceId
            }
            .sortedByDescending {

                it.startTimeMillis
            }
    }


    /* ============================= */
    /* AÑADIR SESIÓN REAL            */
    /* ============================= */

    fun addSession(
        context: Context,
        session: HistorySession
    ) {

        val sessions =
            getSessions(
                context
            )


        sessions.add(
            session
        )


        saveSessions(
            context,
            sessions
        )
    }


    /* ============================= */
    /* BORRAR HISTORIAL DISPOSITIVO  */
    /* ============================= */

    fun removeHistoryForDevice(
        context: Context,
        deviceId: String
    ) {

        val sessions =
            getSessions(
                context
            )


        sessions.removeAll {

            it.deviceId ==
                    deviceId
        }


        saveSessions(
            context,
            sessions
        )
    }


    /* ============================= */
    /* BORRAR TODO                   */
    /* ============================= */

    fun clearHistory(
        context: Context
    ) {

        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .remove(
                KEY_HISTORY
            )
            .remove(
                KEY_DEMO_CREATED
            )
            .remove(
                KEY_DEMO_VERSION
            )
            .apply()
    }


    /* ============================= */
    /* DATOS DE DEMOSTRACIÓN         */
    /* ============================= */

    fun createDemoHistoryIfNeeded(
        context: Context,
        devices: List<ThermoWearDevice>
    ) {

        if (
            devices.isEmpty()
        ) {

            return
        }


        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )


        val currentDemoVersion =
            preferences.getInt(
                KEY_DEMO_VERSION,
                0
            )


        /*
         * Esta versión de los datos demo
         * ya fue creada anteriormente.
         */
        if (
            currentDemoVersion >=
            DEMO_DATA_VERSION
        ) {

            return
        }


        /*
         * Conservamos las sesiones existentes.
         *
         * Así la actualización no destruye
         * el historial creado anteriormente.
         */
        val existingSessions =
            getSessions(
                context
            )


        val demoSessions =
            mutableListOf<HistorySession>()


        val now =
            System.currentTimeMillis()


        devices.forEach {
                device ->


            when (
                device.version
            ) {

                /* ======================== */
                /* FORGE V1                 */
                /* ======================== */

                ThermoWearVersion.V1 -> {

                    createV1DemoHistory(
                        device =
                            device,

                        now =
                            now,

                        destination =
                            demoSessions
                    )
                }


                /* ======================== */
                /* FORGE V2                 */
                /* ======================== */

                ThermoWearVersion.V2 -> {

                    createV2DemoHistory(
                        device =
                            device,

                        now =
                            now,

                        destination =
                            demoSessions
                    )
                }
            }
        }


        val allSessions =
            existingSessions +
                    demoSessions


        saveSessions(
            context,
            allSessions
        )


        preferences
            .edit()
            .putBoolean(
                KEY_DEMO_CREATED,
                true
            )
            .putInt(
                KEY_DEMO_VERSION,
                DEMO_DATA_VERSION
            )
            .apply()
    }


    /* ============================= */
    /* DEMO FORGE V1                 */
    /* ============================= */

    private fun createV1DemoHistory(
        device: ThermoWearDevice,
        now: Long,
        destination:
        MutableList<HistorySession>
    ) {

        /*
         * HOY
         *
         * Sesión reciente de 35 minutos.
         */

        val todayStart1 =
            now -
                    minutesToMillis(
                        55
                    )


        destination.add(

            HistorySession(

                id =
                    UUID.randomUUID()
                        .toString(),

                deviceId =
                    device.id,

                version =
                    device.version,

                startTimeMillis =
                    todayStart1,

                endTimeMillis =
                    todayStart1 +
                            minutesToMillis(
                                35
                            ),

                zones =
                    listOf(

                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.BACK,

                            intensityPeriods =
                                listOf(

                                    createPeriod(
                                        intensity = 1,
                                        start = todayStart1,
                                        durationMinutes = 10
                                    ),

                                    createPeriod(
                                        intensity = 2,
                                        start =
                                            todayStart1 +
                                                    minutesToMillis(
                                                        10
                                                    ),
                                        durationMinutes = 20
                                    ),

                                    createPeriod(
                                        intensity = 3,
                                        start =
                                            todayStart1 +
                                                    minutesToMillis(
                                                        30
                                                    ),
                                        durationMinutes = 5
                                    )
                                )
                        )
                    )
            )
        )


        /*
         * HOY
         *
         * Segunda sesión de 25 minutos.
         */

        val todayStart2 =
            now -
                    minutesToMillis(
                        125
                    )


        destination.add(

            HistorySession(

                id =
                    UUID.randomUUID()
                        .toString(),

                deviceId =
                    device.id,

                version =
                    device.version,

                startTimeMillis =
                    todayStart2,

                endTimeMillis =
                    todayStart2 +
                            minutesToMillis(
                                25
                            ),

                zones =
                    listOf(

                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.BACK,

                            intensityPeriods =
                                listOf(

                                    createPeriod(
                                        intensity = 2,
                                        start = todayStart2,
                                        durationMinutes = 25
                                    )
                                )
                        )
                    )
            )
        )


        /*
         * AYER
         *
         * 50 minutos.
         */

        val yesterdayStart =
            dayAt(
                daysAgo = 1,
                hour = 18,
                minute = 20
            )


        destination.add(

            HistorySession(

                id =
                    UUID.randomUUID()
                        .toString(),

                deviceId =
                    device.id,

                version =
                    device.version,

                startTimeMillis =
                    yesterdayStart,

                endTimeMillis =
                    yesterdayStart +
                            minutesToMillis(
                                50
                            ),

                zones =
                    listOf(

                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.BACK,

                            intensityPeriods =
                                listOf(

                                    createPeriod(
                                        intensity = 1,
                                        start = yesterdayStart,
                                        durationMinutes = 15
                                    ),

                                    createPeriod(
                                        intensity = 2,
                                        start =
                                            yesterdayStart +
                                                    minutesToMillis(
                                                        15
                                                    ),
                                        durationMinutes = 25
                                    ),

                                    createPeriod(
                                        intensity = 3,
                                        start =
                                            yesterdayStart +
                                                    minutesToMillis(
                                                        40
                                                    ),
                                        durationMinutes = 10
                                    )
                                )
                        )
                    )
            )
        )


        /*
         * HACE DOS DÍAS
         */

        val twoDaysAgoStart =
            dayAt(
                daysAgo = 2,
                hour = 16,
                minute = 40
            )


        destination.add(

            HistorySession(

                id =
                    UUID.randomUUID()
                        .toString(),

                deviceId =
                    device.id,

                version =
                    device.version,

                startTimeMillis =
                    twoDaysAgoStart,

                endTimeMillis =
                    twoDaysAgoStart +
                            minutesToMillis(
                                40
                            ),

                zones =
                    listOf(

                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.BACK,

                            intensityPeriods =
                                listOf(

                                    createPeriod(
                                        intensity = 2,
                                        start = twoDaysAgoStart,
                                        durationMinutes = 30
                                    ),

                                    createPeriod(
                                        intensity = 3,
                                        start =
                                            twoDaysAgoStart +
                                                    minutesToMillis(
                                                        30
                                                    ),
                                        durationMinutes = 10
                                    )
                                )
                        )
                    )
            )
        )
    }


    /* ============================= */
    /* DEMO FORGE V2                 */
    /* ============================= */

    private fun createV2DemoHistory(
        device: ThermoWearDevice,
        now: Long,
        destination:
        MutableList<HistorySession>
    ) {

        /*
         * HOY
         *
         * Ambas zonas estuvieron activadas
         * lógicamente durante 40 minutos.
         */

        val todayStart1 =
            now -
                    minutesToMillis(
                        60
                    )


        destination.add(

            HistorySession(

                id =
                    UUID.randomUUID()
                        .toString(),

                deviceId =
                    device.id,

                version =
                    device.version,

                startTimeMillis =
                    todayStart1,

                endTimeMillis =
                    todayStart1 +
                            minutesToMillis(
                                40
                            ),

                zones =
                    listOf(

                        /*
                         * ESPALDA ALTA
                         */

                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.UPPER_BACK,

                            intensityPeriods =
                                listOf(

                                    createPeriod(
                                        intensity = 1,
                                        start = todayStart1,
                                        durationMinutes = 10
                                    ),

                                    createPeriod(
                                        intensity = 2,
                                        start =
                                            todayStart1 +
                                                    minutesToMillis(
                                                        10
                                                    ),
                                        durationMinutes = 20
                                    ),

                                    createPeriod(
                                        intensity = 3,
                                        start =
                                            todayStart1 +
                                                    minutesToMillis(
                                                        30
                                                    ),
                                        durationMinutes = 10
                                    )
                                )
                        ),


                        /*
                         * ESPALDA BAJA
                         */

                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.LOWER_BACK,

                            intensityPeriods =
                                listOf(

                                    createPeriod(
                                        intensity = 2,
                                        start = todayStart1,
                                        durationMinutes = 25
                                    ),

                                    createPeriod(
                                        intensity = 3,
                                        start =
                                            todayStart1 +
                                                    minutesToMillis(
                                                        25
                                                    ),
                                        durationMinutes = 15
                                    )
                                )
                        )
                    )
            )
        )


        /*
         * HOY
         *
         * Segunda sesión.
         *
         * Espalda alta: 35 min.
         * Espalda baja: activa solamente 20 min.
         */

        val todayStart2 =
            now -
                    minutesToMillis(
                        145
                    )


        destination.add(

            HistorySession(

                id =
                    UUID.randomUUID()
                        .toString(),

                deviceId =
                    device.id,

                version =
                    device.version,

                startTimeMillis =
                    todayStart2,

                endTimeMillis =
                    todayStart2 +
                            minutesToMillis(
                                35
                            ),

                zones =
                    listOf(

                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.UPPER_BACK,

                            intensityPeriods =
                                listOf(

                                    createPeriod(
                                        intensity = 2,
                                        start = todayStart2,
                                        durationMinutes = 20
                                    ),

                                    createPeriod(
                                        intensity = 3,
                                        start =
                                            todayStart2 +
                                                    minutesToMillis(
                                                        20
                                                    ),
                                        durationMinutes = 15
                                    )
                                )
                        ),


                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.LOWER_BACK,

                            intensityPeriods =
                                listOf(

                                    createPeriod(
                                        intensity = 1,
                                        start =
                                            todayStart2 +
                                                    minutesToMillis(
                                                        15
                                                    ),
                                        durationMinutes = 10
                                    ),

                                    createPeriod(
                                        intensity = 2,
                                        start =
                                            todayStart2 +
                                                    minutesToMillis(
                                                        25
                                                    ),
                                        durationMinutes = 10
                                    )
                                )
                        )
                    )
            )
        )


        /*
         * AYER
         *
         * Ambas zonas activas 55 minutos.
         */

        val yesterdayStart =
            dayAt(
                daysAgo = 1,
                hour = 19,
                minute = 10
            )


        destination.add(

            HistorySession(

                id =
                    UUID.randomUUID()
                        .toString(),

                deviceId =
                    device.id,

                version =
                    device.version,

                startTimeMillis =
                    yesterdayStart,

                endTimeMillis =
                    yesterdayStart +
                            minutesToMillis(
                                55
                            ),

                zones =
                    listOf(

                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.UPPER_BACK,

                            intensityPeriods =
                                listOf(

                                    createPeriod(
                                        intensity = 1,
                                        start = yesterdayStart,
                                        durationMinutes = 15
                                    ),

                                    createPeriod(
                                        intensity = 2,
                                        start =
                                            yesterdayStart +
                                                    minutesToMillis(
                                                        15
                                                    ),
                                        durationMinutes = 25
                                    ),

                                    createPeriod(
                                        intensity = 3,
                                        start =
                                            yesterdayStart +
                                                    minutesToMillis(
                                                        40
                                                    ),
                                        durationMinutes = 15
                                    )
                                )
                        ),


                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.LOWER_BACK,

                            intensityPeriods =
                                listOf(

                                    createPeriod(
                                        intensity = 2,
                                        start = yesterdayStart,
                                        durationMinutes = 20
                                    ),

                                    createPeriod(
                                        intensity = 3,
                                        start =
                                            yesterdayStart +
                                                    minutesToMillis(
                                                        20
                                                    ),
                                        durationMinutes = 20
                                    ),

                                    createPeriod(
                                        intensity = 2,
                                        start =
                                            yesterdayStart +
                                                    minutesToMillis(
                                                        40
                                                    ),
                                        durationMinutes = 15
                                    )
                                )
                        )
                    )
            )
        )


        /*
         * HACE DOS DÍAS
         *
         * Uso más ligero.
         */

        val twoDaysAgoStart =
            dayAt(
                daysAgo = 2,
                hour = 17,
                minute = 25
            )


        destination.add(

            HistorySession(

                id =
                    UUID.randomUUID()
                        .toString(),

                deviceId =
                    device.id,

                version =
                    device.version,

                startTimeMillis =
                    twoDaysAgoStart,

                endTimeMillis =
                    twoDaysAgoStart +
                            minutesToMillis(
                                45
                            ),

                zones =
                    listOf(

                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.UPPER_BACK,

                            intensityPeriods =
                                listOf(

                                    createPeriod(
                                        intensity = 1,
                                        start = twoDaysAgoStart,
                                        durationMinutes = 15
                                    ),

                                    createPeriod(
                                        intensity = 2,
                                        start =
                                            twoDaysAgoStart +
                                                    minutesToMillis(
                                                        15
                                                    ),
                                        durationMinutes = 30
                                    )
                                )
                        ),


                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.LOWER_BACK,

                            intensityPeriods =
                                listOf(

                                    createPeriod(
                                        intensity = 1,
                                        start =
                                            twoDaysAgoStart +
                                                    minutesToMillis(
                                                        10
                                                    ),
                                        durationMinutes = 20
                                    ),

                                    createPeriod(
                                        intensity = 2,
                                        start =
                                            twoDaysAgoStart +
                                                    minutesToMillis(
                                                        30
                                                    ),
                                        durationMinutes = 15
                                    )
                                )
                        )
                    )
            )
        )
    }


    /* ============================= */
    /* GUARDAR JSON                  */
    /* ============================= */

    private fun saveSessions(
        context: Context,
        sessions: List<HistorySession>
    ) {

        val sessionsArray =
            JSONArray()


        sessions
            .sortedByDescending {
                it.startTimeMillis
            }
            .forEach {
                    session ->


                val sessionItem =
                    JSONObject()


                sessionItem.put(
                    "id",
                    session.id
                )


                sessionItem.put(
                    "deviceId",
                    session.deviceId
                )


                sessionItem.put(
                    "version",
                    session.version.name
                )


                sessionItem.put(
                    "startTimeMillis",
                    session.startTimeMillis
                )


                sessionItem.put(
                    "endTimeMillis",
                    session.endTimeMillis
                )


                val zonesArray =
                    JSONArray()


                session.zones.forEach {
                        zone ->


                    val zoneItem =
                        JSONObject()


                    zoneItem.put(
                        "zone",
                        zone.zone.name
                    )


                    val periodsArray =
                        JSONArray()


                    zone.intensityPeriods.forEach {
                            period ->


                        val periodItem =
                            JSONObject()


                        periodItem.put(
                            "intensity",
                            period.intensity
                        )


                        periodItem.put(
                            "startTimeMillis",
                            period.startTimeMillis
                        )


                        periodItem.put(
                            "endTimeMillis",
                            period.endTimeMillis
                        )


                        periodsArray.put(
                            periodItem
                        )
                    }


                    zoneItem.put(
                        "intensityPeriods",
                        periodsArray
                    )


                    if (
                        zone.averageTemperatureC !=
                        null
                    ) {

                        zoneItem.put(
                            "averageTemperatureC",
                            zone.averageTemperatureC
                        )
                    }


                    zonesArray.put(
                        zoneItem
                    )
                }


                sessionItem.put(
                    "zones",
                    zonesArray
                )


                sessionsArray.put(
                    sessionItem
                )
            }


        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(
                KEY_HISTORY,
                sessionsArray.toString()
            )
            .apply()
    }


    /* ============================= */
    /* CREAR PERIODO                 */
    /* ============================= */

    private fun createPeriod(
        intensity: Int,
        start: Long,
        durationMinutes: Int
    ): IntensityPeriod {

        return IntensityPeriod(

            intensity =
                intensity,

            startTimeMillis =
                start,

            endTimeMillis =
                start +
                        minutesToMillis(
                            durationMinutes
                        )
        )
    }


    /* ============================= */
    /* FECHA DE DÍAS ANTERIORES      */
    /* ============================= */

    private fun dayAt(
        daysAgo: Int,
        hour: Int,
        minute: Int
    ): Long {

        val calendar =
            Calendar.getInstance()


        calendar.add(
            Calendar.DAY_OF_YEAR,
            -daysAgo
        )


        calendar.set(
            Calendar.HOUR_OF_DAY,
            hour
        )


        calendar.set(
            Calendar.MINUTE,
            minute
        )


        calendar.set(
            Calendar.SECOND,
            0
        )


        calendar.set(
            Calendar.MILLISECOND,
            0
        )


        return calendar.timeInMillis
    }


    /* ============================= */
    /* CONVERSIONES                  */
    /* ============================= */

    private fun minutesToMillis(
        minutes: Int
    ): Long {

        return minutes *
                60L *
                1000L
    }
}