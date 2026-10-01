package com.example.thermowear.ui.history


import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.thermowear.R
import com.example.thermowear.data.DeviceManager
import com.example.thermowear.data.HistoryManager
import com.example.thermowear.data.AlertManager
import com.example.thermowear.model.HistorySession
import com.example.thermowear.model.HistoryZoneUsage
import com.example.thermowear.model.IntensityPeriod
import com.example.thermowear.model.ThermoWearDevice
import com.example.thermowear.model.ThermoWearVersion
import com.example.thermowear.model.ThermoWearZone
import com.example.thermowear.ui.alerts.AlertsActivity
import com.example.thermowear.ui.devices.DevicesActivity
import com.example.thermowear.ui.settings.SettingsActivity
import com.example.thermowear.ui.ThermoWearActivity
import com.example.thermowear.util.ScreenTransitions
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class HistoryActivity : ThermoWearActivity() {

    override val isNavigationMenu = true


    private var devices =
        listOf<ThermoWearDevice>()


    private var selectedDevice:
            ThermoWearDevice? =
        null


    private val selectedDate:
            Calendar =
        Calendar.getInstance()


    private lateinit var txtSelectedDevice:
            TextView

    private lateinit var txtSelectedDeviceDetails:
            TextView

    private lateinit var txtSelectedDate:
            TextView

    private lateinit var txtHistorySummaryTitle:
            TextView

    private var selectedDayOfMonth: Int? =
        null

    private lateinit var txtSelectedDay:
            TextView

    private lateinit var btnClearDayFilter:
            TextView

    private lateinit var txtTotalTime:
            TextView

    private lateinit var txtSessionCount:
            TextView

    private lateinit var zonesContainer:
            LinearLayout

    private lateinit var sessionsContainer:
            LinearLayout

    private lateinit var historyContent:
            LinearLayout

    private lateinit var emptyDevicesState:
            LinearLayout

    private lateinit var txtNoSessions:
            TextView


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        setContentView(
            R.layout.activity_history
        )

        com.example.thermowear.util.WindowInsetsHelper.apply(this)


        bindViews()

        configureBottomNavigation()

        configureNavigation()

        configureSelectors()

        loadDevices()
    }


    override fun onResume() {

        super.onResume()


        loadDevices()
        updateAlertIndicator()
    }


    private fun bindViews() {

        txtSelectedDevice =
            findViewById(
                R.id.txtSelectedDevice
            )

        txtSelectedDeviceDetails =
            findViewById(
                R.id.txtSelectedDeviceDetails
            )

        txtSelectedDate =
            findViewById(
                R.id.txtSelectedDate
            )

        txtHistorySummaryTitle =
            findViewById(
                R.id.txtHistorySummaryTitle
            )

        txtSelectedDay =
            findViewById(
                R.id.txtSelectedDay
            )

        btnClearDayFilter =
            findViewById(
                R.id.btnClearDayFilter
            )

        txtTotalTime =
            findViewById(
                R.id.txtTotalTime
            )

        txtSessionCount =
            findViewById(
                R.id.txtSessionCount
            )

        zonesContainer =
            findViewById(
                R.id.zonesContainer
            )

        sessionsContainer =
            findViewById(
                R.id.sessionsContainer
            )

        historyContent =
            findViewById(
                R.id.historyContent
            )

        emptyDevicesState =
            findViewById(
                R.id.emptyDevicesState
            )

        txtNoSessions =
            findViewById(
                R.id.txtNoSessions
            )
    }


    private fun loadDevices() {

        devices =
            DeviceManager.getDevices(
                this
            )


        if (devices.isEmpty()) {

            selectedDevice =
                null


            showNoDevicesState()

            return
        }


        val previousDevice =
            selectedDevice


        selectedDevice =
            if (
                previousDevice != null &&
                devices.any {
                    it.id ==
                            previousDevice.id
                }
            ) {

                devices.first {
                    it.id ==
                            previousDevice.id
                }

            } else {

                devices.first()
            }


        emptyDevicesState.visibility =
            View.GONE


        historyContent.visibility =
            View.VISIBLE


        updateDeviceSelector()

        updateDateSelector()

        refreshHistory()
    }


    private fun showNoDevicesState() {

        txtSelectedDevice.text =
            "Sin dispositivos"


        txtSelectedDeviceDetails.text =
            "Añade una chamarra Forge desde Inicio"


        emptyDevicesState.visibility =
            View.VISIBLE


        historyContent.visibility =
            View.GONE
    }


    private fun configureSelectors() {

        val deviceSelector =
            findViewById<MaterialCardView>(
                R.id.cardDeviceSelector
            )


        deviceSelector
            .setOnClickListener {

                showDeviceMenu(
                    deviceSelector
                )
            }


        val dateSelector =
            findViewById<MaterialCardView>(
                R.id.cardDateSelector
            )


        dateSelector
            .setOnClickListener {
                showMonthPicker()
            }

        findViewById<MaterialCardView>(
            R.id.cardDayFilter
        ).setOnClickListener {
            showDayPicker()
        }

        btnClearDayFilter.setOnClickListener {
            selectedDayOfMonth = null
            updateDateSelector()
            refreshHistory()
        }
    }


    private fun showDeviceMenu(
        anchor: View
    ) {

        if (devices.isEmpty()) {

            Toast.makeText(
                this,
                "No hay dispositivos agregados.",
                Toast.LENGTH_SHORT
            ).show()


            return
        }


        val menu =
            PopupMenu(
                this,
                anchor
            )


        devices.forEachIndexed {
                index,
                device ->


            val version =
                when (device.version) {

                    ThermoWearVersion.V1 ->
                        "V1"

                    ThermoWearVersion.V2 ->
                        "V2"

                    ThermoWearVersion.V3 ->
                        "V2"
                }


            menu.menu.add(
                0,
                index,
                index,
                "Chamarra Forge $version"
            )
        }


        menu.setOnMenuItemClickListener {
                menuItem ->


            val index =
                menuItem.itemId


            if (
                index !in devices.indices
            ) {

                return@setOnMenuItemClickListener false
            }


            selectedDevice =
                devices[index]


            updateDeviceSelector()

            refreshHistory()


            true
        }


        menu.show()
    }


    private fun showMonthPicker() {
        val locale = Locale("es", "BO")
        val monthLabels = (0..11).map { month ->
            val monthCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2020)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            SimpleDateFormat("MMMM", locale)
                .format(monthCalendar.time)
                .replaceFirstChar { it.titlecase(locale) }
        }.toTypedArray()

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val monthPicker = NumberPicker(this).apply {
            wrapSelectorWheel = false
        }
        val yearPicker = NumberPicker(this).apply {
            minValue = 2000
            maxValue = currentYear
            value = selectedDate.get(Calendar.YEAR).coerceIn(2000, currentYear)
            wrapSelectorWheel = false
        }

        fun updateMonthRange(year: Int, preferredMonth: Int) {
            val maxMonth = if (year == currentYear) {
                Calendar.getInstance().get(Calendar.MONTH)
            } else {
                Calendar.DECEMBER
            }
            monthPicker.displayedValues = null
            monthPicker.minValue = Calendar.JANUARY
            monthPicker.maxValue = maxMonth
            monthPicker.displayedValues = monthLabels.copyOfRange(0, maxMonth + 1)
            monthPicker.value = preferredMonth.coerceIn(Calendar.JANUARY, maxMonth)
        }

        updateMonthRange(
            yearPicker.value,
            selectedDate.get(Calendar.MONTH)
        )
        yearPicker.setOnValueChangedListener { _, _, year ->
            updateMonthRange(year, monthPicker.value)
        }

        val monthLabel = TextView(this).apply {
            text = "Mes"
            textSize = 14f
            setTextColor(getColor(R.color.tw_text_secondary))
        }
        val yearLabel = TextView(this).apply {
            text = "Año"
            textSize = 14f
            setTextColor(getColor(R.color.tw_text_secondary))
        }
        val pickerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(
                monthPicker,
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            )
            addView(
                yearPicker,
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            )
        }
        val pickerContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val horizontalPadding = (24 * resources.displayMetrics.density).toInt()
            val verticalPadding = (8 * resources.displayMetrics.density).toInt()
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, 0)
            addView(
                LinearLayout(this@HistoryActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    addView(monthLabel, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
                    addView(yearLabel, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
                }
            )
            addView(pickerRow)
        }

        AlertDialog.Builder(this)
            .setTitle("Seleccionar mes")
            .setView(pickerContent)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Aplicar") { _, _ ->
                selectedDate.set(Calendar.YEAR, yearPicker.value)
                selectedDate.set(Calendar.MONTH, monthPicker.value)
                selectedDate.set(Calendar.DAY_OF_MONTH, 1)
                selectedDayOfMonth = null
                updateDateSelector()
                refreshHistory()
            }
            .show()
    }


    private fun showDayPicker() {
        val initialDay = selectedDayOfMonth
            ?: selectedDate.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            this,
            { _, _, _, day ->
                selectedDayOfMonth = day
                updateDateSelector()
                refreshHistory()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            initialDay
        )

        val monthStart = (selectedDate.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val monthEnd = (monthStart.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
            add(Calendar.MILLISECOND, -1)
        }

        dialog.datePicker.minDate = monthStart.timeInMillis
        dialog.datePicker.maxDate =
            minOf(monthEnd.timeInMillis, System.currentTimeMillis())
        dialog.show()
    }


    private fun updateDeviceSelector() {

        val device =
            selectedDevice
                ?: return


        val version =
            when (device.version) {

                ThermoWearVersion.V1 ->
                    "V1"

                ThermoWearVersion.V2 ->
                    "V2"

                ThermoWearVersion.V3 ->
                    "V2"
            }


        txtSelectedDevice.text =
            "Chamarra Forge $version"


        txtSelectedDeviceDetails.text =
            when (device.version) {

                ThermoWearVersion.V1 ->

                    "Forge V1 • 1 zona"

                ThermoWearVersion.V2 ->

                    "Forge V2 • 2 zonas"

                ThermoWearVersion.V3 ->

                    "Forge V2 • 2 zonas"
            }
    }


    private fun updateDateSelector() {
        val locale = Locale("es", "BO")
        val monthName = SimpleDateFormat("MMMM yyyy", locale)
            .format(selectedDate.time)

        txtSelectedDate.text =
            "Mes: $monthName"

        txtSelectedDay.text =
            selectedDayOfMonth?.let { day ->
                val selectedDayDate = (selectedDate.clone() as Calendar).apply {
                    set(Calendar.DAY_OF_MONTH, day)
                }
                "Día: " + SimpleDateFormat("dd MMM yyyy", locale)
                    .format(selectedDayDate.time)
            } ?: "Filtrar por día (opcional)"

        btnClearDayFilter.visibility =
            if (selectedDayOfMonth == null) View.GONE else View.VISIBLE

        txtHistorySummaryTitle.text =
            if (selectedDayOfMonth == null) {
                "Uso del mes"
            } else {
                "Uso del día"
            }

        txtNoSessions.text =
            if (selectedDayOfMonth == null) {
                "No hay sesiones registradas para este mes."
            } else {
                "No hay sesiones registradas para este día."
            }
    }


    private fun refreshHistory() {
        val device = selectedDevice ?: return
        val sessions = HistoryManager
            .getSessionsForDevice(this, device.id)
            .filter { session ->
                val sessionDate = Calendar.getInstance().apply {
                    timeInMillis = session.startTimeMillis
                }
                sessionDate.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                    sessionDate.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                    (selectedDayOfMonth == null ||
                        sessionDate.get(Calendar.DAY_OF_MONTH) == selectedDayOfMonth)
            }

        renderSummary(sessions)
        renderZones(device, sessions)
        renderSessions(sessions)
    }


    /**
     * Tiempo total de uso de la chamarra.
     *
     * Aquí NO sumamos las zonas.
     */
    private fun renderSummary(
        sessions: List<HistorySession>
    ) {

        val totalDuration =
            sessions.sumOf {
                it.durationMillis
            }


        txtTotalTime.text =
            formatDuration(
                totalDuration
            )


        txtSessionCount.text =
            sessions.size
                .toString()
    }


    private fun renderZones(
        device: ThermoWearDevice,
        sessions: List<HistorySession>
    ) {

        zonesContainer
            .removeAllViews()


        val zones =
            when (device.version) {

                ThermoWearVersion.V1 ->

                    listOf(
                        ThermoWearZone.BACK
                    )


                ThermoWearVersion.V2, ThermoWearVersion.V3 ->

                    listOf(
                        ThermoWearZone.UPPER_BACK,
                        ThermoWearZone.LOWER_BACK
                    )
            }


        val maximumDuration =
            zones.maxOfOrNull {
                    zone ->

                getTotalZoneDuration(
                    sessions,
                    zone
                )

            } ?: 0L


        zones.forEach { zone ->

            zonesContainer.addView(

                createZoneCard(

                    zone =
                        zone,

                    duration =
                        getTotalZoneDuration(
                            sessions,
                            zone
                        ),

                    dominantIntensity =
                        getMostUsedIntensity(
                            sessions,
                            zone
                        ),

                    maximumDuration =
                        maximumDuration
                )
            )
        }
    }


    private fun createZoneCard(
        zone: ThermoWearZone,
        duration: Long,
        dominantIntensity: Int?,
        maximumDuration: Long
    ): MaterialCardView {

        val card =
            MaterialCardView(
                this
            )


        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        cardParams.bottomMargin =
            12.dp


        card.layoutParams =
            cardParams


        card.radius =
            18.dp.toFloat()


        card.cardElevation =
            0f


        card.setCardBackgroundColor(
            getColor(
                R.color.white
            )
        )


        val content =
            LinearLayout(
                this
            )


        content.orientation =
            LinearLayout.VERTICAL


        content.setPadding(
            20.dp,
            18.dp,
            20.dp,
            18.dp
        )


        val title =
            TextView(
                this
            )


        title.text =
            zone.displayName


        title.textSize =
            17f


        title.setTypeface(
            title.typeface,
            Typeface.BOLD
        )


        title.setTextColor(
            getColor(
                R.color.tw_text
            )
        )


        content.addView(
            title
        )


        val durationView =
            TextView(
                this
            )


        durationView.text =
            if (duration > 0L) {

                formatDuration(
                    duration
                )

            } else {

                "Sin uso"
            }


        durationView.textSize =
            25f


        durationView.setTypeface(
            durationView.typeface,
            Typeface.BOLD
        )


        durationView.setTextColor(
            getColor(
                R.color.tw_primary
            )
        )


        val durationParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        durationParams.topMargin =
            8.dp


        durationView.layoutParams =
            durationParams


        content.addView(
            durationView
        )


        val intensityView =
            TextView(
                this
            )


        intensityView.text =
            if (
                dominantIntensity != null
            ) {

                "Intensidad más usada: ${
                    getIntensityLabel(
                        dominantIntensity
                    )
                }"

            } else {

                "Sin intensidad registrada"
            }


        intensityView.textSize =
            13f


        intensityView.setTextColor(
            getColor(
                R.color.tw_text_secondary
            )
        )


        val intensityParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        intensityParams.topMargin =
            5.dp


        intensityView.layoutParams =
            intensityParams


        content.addView(
            intensityView
        )


        val progress =
            ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
            )


        progress.max =
            100


        progress.progress =
            if (
                maximumDuration > 0L
            ) {

                (
                        duration.toDouble() /
                                maximumDuration.toDouble() *
                                100.0
                        ).toInt()

            } else {

                0
            }


        progress.progressTintList =
            ColorStateList.valueOf(
                getColor(
                    R.color.tw_primary
                )
            )


        progress.progressBackgroundTintList =
            ColorStateList.valueOf(
                Color.parseColor(
                    "#E7E7ED"
                )
            )


        val progressParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8.dp
            )


        progressParams.topMargin =
            14.dp


        progress.layoutParams =
            progressParams


        content.addView(
            progress
        )


        card.addView(
            content
        )


        return card
    }


    private fun renderSessions(
        sessions: List<HistorySession>
    ) {

        sessionsContainer
            .removeAllViews()


        if (sessions.isEmpty()) {

            txtNoSessions.visibility =
                View.VISIBLE


            return
        }


        txtNoSessions.visibility =
            View.GONE


        sessions
            .sortedByDescending {
                it.startTimeMillis
            }
            .forEach { session ->

                sessionsContainer.addView(

                    createSessionCard(
                        session
                    )
                )
            }
    }


    private fun createSessionCard(
        session: HistorySession
    ): MaterialCardView {

        val card =
            MaterialCardView(
                this
            )


        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        cardParams.bottomMargin =
            12.dp


        card.layoutParams =
            cardParams


        card.radius =
            18.dp.toFloat()


        card.cardElevation =
            0f


        card.setCardBackgroundColor(
            getColor(
                R.color.white
            )
        )


        val content =
            LinearLayout(
                this
            )


        content.orientation =
            LinearLayout.VERTICAL


        content.setPadding(
            20.dp,
            18.dp,
            20.dp,
            18.dp
        )


        val timeFormatter =
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            )


        val start =
            timeFormatter.format(
                session.startTimeMillis
            )


        val end =
            timeFormatter.format(
                session.endTimeMillis
            )


        val title =
            TextView(
                this
            )


        title.text =
            "$start - $end"


        title.textSize =
            17f


        title.setTypeface(
            title.typeface,
            Typeface.BOLD
        )


        title.setTextColor(
            getColor(
                R.color.tw_text
            )
        )


        content.addView(
            title
        )


        val duration =
            TextView(
                this
            )


        duration.text =
            "Duración de la sesión: ${
                formatDuration(
                    session.durationMillis
                )
            }"


        duration.textSize =
            13f


        duration.setTextColor(
            getColor(
                R.color.tw_text_secondary
            )
        )


        val durationParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        durationParams.topMargin =
            4.dp


        duration.layoutParams =
            durationParams


        content.addView(
            duration
        )


        session.zones
            .filter {

                it.activeDurationMillis >
                        0L
            }
            .forEach { usage ->

                addZoneUsageToSession(
                    content,
                    usage
                )
            }


        card.addView(
            content
        )


        return card
    }


    private fun addZoneUsageToSession(
        container: LinearLayout,
        usage: HistoryZoneUsage
    ) {

        val mainText =
            TextView(
                this
            )


        mainText.text =
            "• ${usage.zone.displayName}  •  ${
                formatDuration(
                    usage.activeDurationMillis
                )
            }"


        mainText.textSize =
            13f


        mainText.setTypeface(
            mainText.typeface,
            Typeface.BOLD
        )


        mainText.setTextColor(
            getColor(
                R.color.tw_text
            )
        )


        val mainParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        mainParams.topMargin =
            12.dp


        mainText.layoutParams =
            mainParams


        container.addView(
            mainText
        )


        val intensitySummary =
            TextView(
                this
            )


        intensitySummary.text =
            buildIntensitySummary(
                usage.intensityPeriods
            )


        intensitySummary.textSize =
            12f


        intensitySummary.setTextColor(
            getColor(
                R.color.tw_text_secondary
            )
        )


        val summaryParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        summaryParams.topMargin =
            4.dp


        intensitySummary.layoutParams =
            summaryParams


        container.addView(
            intensitySummary
        )
    }


    private fun buildIntensitySummary(
        periods: List<IntensityPeriod>
    ): String {

        if (periods.isEmpty()) {

            return "Sin intensidad registrada"
        }


        val low =
            periods
                .filter {
                    it.intensity == 1
                }
                .sumOf {
                    it.durationMillis
                }


        val medium =
            periods
                .filter {
                    it.intensity == 2
                }
                .sumOf {
                    it.durationMillis
                }


        val high =
            periods
                .filter {
                    it.intensity == 3
                }
                .sumOf {
                    it.durationMillis
                }


        val parts =
            mutableListOf<String>()


        if (low > 0L) {

            parts.add(
                "Bajo ${formatDuration(low)}"
            )
        }


        if (medium > 0L) {

            parts.add(
                "Medio ${formatDuration(medium)}"
            )
        }


        if (high > 0L) {

            parts.add(
                "Alto ${formatDuration(high)}"
            )
        }


        return parts.joinToString(
            " · "
        )
    }


    private fun getTotalZoneDuration(
        sessions: List<HistorySession>,
        zone: ThermoWearZone
    ): Long {

        return sessions
            .flatMap {
                it.zones
            }
            .filter {
                it.zone == zone
            }
            .sumOf {
                it.activeDurationMillis
            }
    }


    private fun getMostUsedIntensity(
        sessions: List<HistorySession>,
        zone: ThermoWearZone
    ): Int? {

        val periods =
            sessions
                .flatMap {
                    it.zones
                }
                .filter {
                    it.zone == zone
                }
                .flatMap {
                    it.intensityPeriods
                }


        if (periods.isEmpty()) {

            return null
        }


        return periods
            .groupBy {
                it.intensity
            }
            .mapValues {
                    entry ->

                entry.value.sumOf {
                    it.durationMillis
                }
            }
            .maxByOrNull {
                it.value
            }
            ?.key
    }


    private fun getIntensityLabel(
        intensity: Int
    ): String {

        return when (intensity) {

            1 -> "Bajo"

            2 -> "Medio"

            3 -> "Alto"

            else -> "Sin definir"
        }
    }


    private fun formatDuration(
        millis: Long
    ): String {

        val totalSeconds =
            millis /
                    1000L


        if (totalSeconds < 60L) {

            return "${totalSeconds}s"
        }


        val totalMinutes =
            totalSeconds /
                    60L


        val hours =
            totalMinutes /
                    60L


        val minutes =
            totalMinutes %
                    60L


        return when {

            hours > 0L &&
                    minutes > 0L ->

                "${hours}h ${minutes}m"


            hours > 0L ->

                "${hours}h"


            else ->

                "${minutes} min"
        }
    }


    private fun isSameDay(
        timeMillis: Long,
        calendar: Calendar
    ): Boolean {

        val date =
            Calendar.getInstance()


        date.timeInMillis =
            timeMillis


        return isSameDay(
            date,
            calendar
        )
    }


    private fun isSameDay(
        first: Calendar,
        second: Calendar
    ): Boolean {

        return first.get(
            Calendar.YEAR
        ) ==
                second.get(
                    Calendar.YEAR
                ) &&

                first.get(
                    Calendar.DAY_OF_YEAR
                ) ==
                second.get(
                    Calendar.DAY_OF_YEAR
                )
    }


    private val Int.dp: Int
        get() = (
                this *
                        resources
                            .displayMetrics
                            .density
                ).toInt()


    override fun finish() {

        super.finish()

        ScreenTransitions.closeFade(
            this
        )
    }


    /* ============================ */
    /* BOTTOM NAVIGATION            */
    /* ============================ */

    private fun configureNavigation() {

        val navHome =
            findViewById<LinearLayout>(
                R.id.navHome
            )


        navHome.setOnClickListener {

            val intent =
                Intent(
                    this,
                    DevicesActivity::class.java
                )


            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP


            ScreenTransitions.openModule(
                this,
                intent
            )
        }


        val navAlerts =
            findViewById<LinearLayout>(
                R.id.navAlerts
            )


        navAlerts.setOnClickListener {

            ScreenTransitions.openModule(
                this,
                Intent(
                    this,
                    AlertsActivity::class.java
                )
            )
        }


        val navSettings =
            findViewById<LinearLayout>(
                R.id.navSettings
            )


        navSettings.setOnClickListener {

            ScreenTransitions.openModule(
                this,
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
    }


    private fun configureBottomNavigation() {

        val background =
            findViewById<LinearLayout>(
                R.id.bgNavHistory
            )


        val icon =
            findViewById<ImageView>(
                R.id.iconNavHistory
            )


        val text =
            findViewById<TextView>(
                R.id.txtNavHistory
            )


        background.setBackgroundResource(
            R.drawable.bg_nav_selected
        )


        icon.setColorFilter(
            getColor(
                R.color.tw_text
            )
        )


        text.setTextColor(
            getColor(
                R.color.tw_text
            )
        )
    }


    private fun updateAlertIndicator() {

        val alertDot =
            findViewById<View>(
                R.id.alertDot
            )


        alertDot.visibility =
            if (
                AlertManager.hasUnreadAlerts(
                    this
                )
            ) {

                View.VISIBLE

            } else {

                View.GONE
            }
    }
}
