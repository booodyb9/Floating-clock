package com.example.model

data class LapRecord(
    val lapIndex: Int,
    val lapTimeMs: Long,
    val totalTimeMs: Long
)

enum class TimerMode {
    STOPWATCH,
    COUNTDOWN_FOCUS
}

data class TimerState(
    val mode: TimerMode = TimerMode.STOPWATCH,
    val isRunning: Boolean = false,
    val elapsedTimeMs: Long = 0L,
    val countdownTotalSeconds: Int = 25 * 60,
    val countdownRemainingSeconds: Int = 25 * 60,
    val laps: List<LapRecord> = emptyList()
)
