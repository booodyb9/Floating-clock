package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Clock3DSettings
import com.example.model.LapRecord
import com.example.model.TimerMode
import com.example.model.TimerState
import com.example.model.WorldCitiesData
import com.example.model.WorldCity
import com.example.service.FloatingManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ClockViewModel(application: Application) : AndroidViewModel(application) {

    // Current Time Floats
    private val _hours = MutableStateFlow(10.15f)
    val hours: StateFlow<Float> = _hours.asStateFlow()

    private val _minutes = MutableStateFlow(9.2f)
    val minutes: StateFlow<Float> = _minutes.asStateFlow()

    private val _seconds = MutableStateFlow(30f)
    val seconds: StateFlow<Float> = _seconds.asStateFlow()

    // Formatted Strings
    private val _digitalTimeText = MutableStateFlow("10:09:30")
    val digitalTimeText: StateFlow<String> = _digitalTimeText.asStateFlow()

    private val _gregorianDateText = MutableStateFlow("")
    val gregorianDateText: StateFlow<String> = _gregorianDateText.asStateFlow()

    private val _hijriDateText = MutableStateFlow("")
    val hijriDateText: StateFlow<String> = _hijriDateText.asStateFlow()

    // 3D Clock Settings
    private val _settings = MutableStateFlow(Clock3DSettings())
    val settings: StateFlow<Clock3DSettings> = _settings.asStateFlow()

    // Language: default Arabic
    private val _isArabic = MutableStateFlow(true)
    val isArabic: StateFlow<Boolean> = _isArabic.asStateFlow()

    // Timer / Stopwatch State
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    // World Cities
    private val _worldCities = MutableStateFlow(WorldCitiesData.defaultCities)
    val worldCities: StateFlow<List<WorldCity>> = _worldCities.asStateFlow()

    init {
        // Start high-precision smooth or quartz clock ticker loop
        viewModelScope.launch {
            while (isActive) {
                val cal = Calendar.getInstance()
                val secInt = cal.get(Calendar.SECOND)
                val millis = cal.get(Calendar.MILLISECOND)
                val minInt = cal.get(Calendar.MINUTE)
                val hourInt = cal.get(Calendar.HOUR)

                val smooth = _settings.value.smoothSweepSeconds
                val secFloat = if (smooth) secInt + (millis / 1000f) else secInt.toFloat()
                val minFloat = minInt + (secFloat / 60f)
                val hourFloat = hourInt + (minFloat / 60f)

                _seconds.value = secFloat
                _minutes.value = minFloat
                _hours.value = hourFloat

                // Formatted Time
                val hourFormat = if (_settings.value.is24Hour) "HH:mm:ss" else "hh:mm:ss a"
                val timeSdf = SimpleDateFormat(hourFormat, if (_isArabic.value) Locale.forLanguageTag("ar") else Locale.US)
                _digitalTimeText.value = timeSdf.format(cal.time)

                // Gregorian Date
                val dateSdf = SimpleDateFormat("EEEE, d MMMM yyyy", if (_isArabic.value) Locale.forLanguageTag("ar") else Locale.US)
                _gregorianDateText.value = dateSdf.format(cal.time)

                // Approximate Hijri representation
                val hijriApprox = calculateApproxHijri(cal)
                _hijriDateText.value = hijriApprox

                // Delay interval: 30ms for smooth sweep, 500ms for quartz tick
                delay(if (smooth) 32L else 500L)
            }
        }

        // Timer loop
        viewModelScope.launch {
            var lastTick = System.currentTimeMillis()
            while (isActive) {
                val now = System.currentTimeMillis()
                val delta = now - lastTick
                lastTick = now

                if (_timerState.value.isRunning) {
                    if (_timerState.value.mode == TimerMode.STOPWATCH) {
                        _timerState.update { it.copy(elapsedTimeMs = it.elapsedTimeMs + delta) }
                    } else {
                        // Countdown
                        val remainingMs = (_timerState.value.countdownRemainingSeconds * 1000L) - delta
                        if (remainingMs <= 0) {
                            _timerState.update { it.copy(isRunning = false, countdownRemainingSeconds = 0) }
                        } else {
                            _timerState.update { it.copy(countdownRemainingSeconds = (remainingMs / 1000L).toInt()) }
                        }
                    }
                }
                delay(50L)
            }
        }
    }

    fun toggleLanguage() {
        _isArabic.value = !_isArabic.value
    }

    fun updateSettings(newSettings: Clock3DSettings) {
        _settings.value = newSettings
        FloatingManager.currentSettings = newSettings
    }

    fun updateHandColor(color: Color) {
        val newHandColors = _settings.value.handColors.copy(
            hourColor = color,
            minuteColor = color,
            secondColor = color
        )
        updateSettings(_settings.value.copy(handColors = newHandColors))
    }

    fun updateBackgroundColor(color: Color) {
        val newBg = _settings.value.background.copy(primaryColor = color)
        updateSettings(_settings.value.copy(background = newBg))
    }

    fun toggleFloatingOverlay() {
        val context = getApplication<Application>().applicationContext
        if (FloatingManager.isFloatingActive || FloatingManager.isInAppFloatingActive) {
            FloatingManager.stopFloatingService(context)
        } else {
            FloatingManager.startFloatingService(context, _settings.value)
        }
    }

    fun closeInAppFloating() {
        FloatingManager.isInAppFloatingActive = false
    }

    // Stopwatch Controls
    fun startTimer() {
        _timerState.update { it.copy(isRunning = true) }
    }

    fun pauseTimer() {
        _timerState.update { it.copy(isRunning = false) }
    }

    fun resetTimer() {
        _timerState.update {
            it.copy(
                isRunning = false,
                elapsedTimeMs = 0L,
                countdownRemainingSeconds = it.countdownTotalSeconds,
                laps = emptyList()
            )
        }
    }

    fun recordLap() {
        val currentState = _timerState.value
        val lapCount = currentState.laps.size + 1
        val lastTotal = currentState.laps.firstOrNull()?.totalTimeMs ?: 0L
        val lapTime = currentState.elapsedTimeMs - lastTotal
        val newRecord = LapRecord(lapCount, lapTime, currentState.elapsedTimeMs)
        _timerState.update { it.copy(laps = listOf(newRecord) + it.laps) }
    }

    fun setTimerMode(mode: TimerMode) {
        _timerState.update { it.copy(mode = mode, isRunning = false) }
    }

    fun setCountdownMinutes(minutes: Int) {
        val totalSec = minutes * 60
        _timerState.update {
            it.copy(
                countdownTotalSeconds = totalSec,
                countdownRemainingSeconds = totalSec,
                isRunning = false
            )
        }
    }

    private fun calculateApproxHijri(cal: Calendar): String {
        // Approximate calculation for Gregorian 2026/2027 to Hijri 1448
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        // Saudi Umm al-Qura standard alignment
        val hijriYear = ((year - 622) * 1.030684).toInt()
        val hijriMonthsAr = listOf("محرم", "صفر", "ربيع الأول", "ربيع الثاني", "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة")
        val hijriMonthsEn = listOf("Muharram", "Safar", "Rabi I", "Rabi II", "Jumada I", "Jumada II", "Rajab", "Sha'ban", "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah")

        val approxMonthIdx = ((month + 8) % 12)
        return if (_isArabic.value) {
            "$day ${hijriMonthsAr[approxMonthIdx]} $hijriYear هـ"
        } else {
            "$day ${hijriMonthsEn[approxMonthIdx]} $hijriYear AH"
        }
    }

    fun getCityFormattedTime(city: WorldCity): String {
        val cal = Calendar.getInstance(city.timeZone)
        val hourFormat = if (_settings.value.is24Hour) "HH:mm" else "hh:mm a"
        val sdf = SimpleDateFormat(hourFormat, if (_isArabic.value) Locale.forLanguageTag("ar") else Locale.US)
        sdf.timeZone = city.timeZone
        return sdf.format(cal.time)
    }
}
