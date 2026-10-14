package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TimerMode
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.GlassBlue
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SurfaceCard
import com.example.viewmodel.ClockViewModel

@Composable
fun StopwatchTimerScreen(
    viewModel: ClockViewModel,
    modifier: Modifier = Modifier
) {
    val timerState by viewModel.timerState.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("stopwatch_timer_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tab Selector: Stopwatch vs Countdown
        TabRow(
            selectedTabIndex = if (timerState.mode == TimerMode.STOPWATCH) 0 else 1,
            containerColor = Color.Transparent,
            contentColor = RoyalGold,
            indicator = { tabPositions ->
                val idx = if (timerState.mode == TimerMode.STOPWATCH) 0 else 1
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[idx]),
                    color = RoyalGold,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = timerState.mode == TimerMode.STOPWATCH,
                onClick = { viewModel.setTimerMode(TimerMode.STOPWATCH) },
                text = {
                    Text(
                        text = if (isArabic) "ساعة الإيقاف 3D" else "3D Stopwatch",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                icon = { Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = timerState.mode == TimerMode.COUNTDOWN_FOCUS,
                onClick = { viewModel.setTimerMode(TimerMode.COUNTDOWN_FOCUS) },
                text = {
                    Text(
                        text = if (isArabic) "مؤقت التركيز (بومودورو)" else "Focus Countdown",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                icon = { Icon(Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Big Circular 3D Dial Display
        Box(
            modifier = Modifier
                .size(230.dp)
                .shadow(24.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            settings.background.primaryColor.copy(alpha = 0.95f),
                            ObsidianBlack
                        )
                    )
                )
                .border(3.dp, settings.background.rimGlowColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (timerState.mode == TimerMode.COUNTDOWN_FOCUS) {
                val progress = if (timerState.countdownTotalSeconds > 0) {
                    timerState.countdownRemainingSeconds.toFloat() / timerState.countdownTotalSeconds
                } else 0f

                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(220.dp),
                    color = RoyalGold,
                    trackColor = Color.White.copy(alpha = 0.1f),
                    strokeWidth = 6.dp
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (timerState.mode == TimerMode.STOPWATCH) {
                    val totalSec = timerState.elapsedTimeMs / 1000
                    val minutes = totalSec / 60
                    val seconds = totalSec % 60
                    val millis = (timerState.elapsedTimeMs % 1000) / 10

                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    )
                    Text(
                        text = String.format(".%02d", millis),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = settings.handColors.secondColor
                    )
                } else {
                    val minutes = timerState.countdownRemainingSeconds / 60
                    val seconds = timerState.countdownRemainingSeconds % 60

                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = RoyalGold
                    )
                    Text(
                        text = if (isArabic) "مؤقت إنتاجية فاخر" else "Productivity Focus",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Preset Minutes for Countdown
        if (timerState.mode == TimerMode.COUNTDOWN_FOCUS) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf(5, 15, 25, 45).forEach { min ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (timerState.countdownTotalSeconds == min * 60) RoyalGold else Color.White.copy(alpha = 0.08f)
                            )
                            .clickable { viewModel.setCountdownMinutes(min) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$min m",
                            color = if (timerState.countdownTotalSeconds == min * 60) ObsidianBlack else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action Buttons Row: Start/Pause, Lap, Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (timerState.isRunning) viewModel.pauseTimer() else viewModel.startTimer()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("timer_start_pause_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (timerState.isRunning) Color(0xFFF43F5E) else EmeraldGlow,
                    contentColor = ObsidianBlack
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (timerState.isRunning) {
                        if (isArabic) "إيقاف مؤقت" else "Pause"
                    } else {
                        if (isArabic) "بدء" else "Start"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            if (timerState.mode == TimerMode.STOPWATCH) {
                Button(
                    onClick = { viewModel.recordLap() },
                    enabled = timerState.isRunning,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("lap_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GlassBlue,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "تسجيل دورة" else "Lap",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            OutlinedButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .weight(0.7f)
                    .height(50.dp)
                    .testTag("timer_reset_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.3f)))
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Laps List (if stopwatch)
        if (timerState.mode == TimerMode.STOPWATCH && timerState.laps.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard.copy(alpha = 0.6f))
                    .padding(8.dp)
            ) {
                items(timerState.laps) { lap ->
                    val lapSec = lap.lapTimeMs / 1000
                    val lapMin = lapSec / 60
                    val lapRemSec = lapSec % 60
                    val lapMil = (lap.lapTimeMs % 1000) / 10

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "الدورة #${lap.lapIndex}" else "Lap #${lap.lapIndex}",
                            color = RoyalGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = String.format("%02d:%02d.%02d", lapMin, lapRemSec, lapMil),
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
