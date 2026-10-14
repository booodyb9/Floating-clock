package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.service.FloatingManager
import com.example.ui.clock3d.Clock3DView
import com.example.ui.components.ClockStyleSelector
import com.example.ui.components.FullCustomizationPanel
import com.example.ui.components.SimpleQuickPaletteBar
import com.example.ui.theme.GlassBlue
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SurfaceCard
import com.example.viewmodel.ClockViewModel

@Composable
fun ClockStudioScreen(
    viewModel: ClockViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val hours by viewModel.hours.collectAsState()
    val minutes by viewModel.minutes.collectAsState()
    val seconds by viewModel.seconds.collectAsState()
    val digitalTime by viewModel.digitalTimeText.collectAsState()
    val gregorianDate by viewModel.gregorianDateText.collectAsState()
    val hijriDate by viewModel.hijriDateText.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()

    var showDeepCustomizer by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("clock_studio_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero 3D Stage Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .testTag("hero_3d_stage"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        settings.background.rimGlowColor.copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                settings.background.primaryColor.copy(alpha = 0.85f),
                                ObsidianBlack
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Interactive 3D Clock View
                Clock3DView(
                    settings = settings,
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    isInteractive = true,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Gesture Hint Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isArabic) "اسحب للتمرير بزاوية 3D • نقرتين لإعادة التعيين" else "Drag to rotate 3D • Double-tap to reset",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }

                // Bottom Digital Bar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(1.dp, settings.background.rimGlowColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = digitalTime,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = settings.handColors.hourColor
                        )
                        Text(
                            text = if (settings.showHijriDate) "$hijriDate • $gregorianDate" else gregorianDate,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main CTA: Launch 3D Floating Clock Overlay Button
        val isOverlayActive = FloatingManager.isFloatingActive || FloatingManager.isInAppFloatingActive
        Button(
            onClick = { viewModel.toggleFloatingOverlay() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("launch_floating_clock_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isOverlayActive) GlassBlue else RoyalGold,
                contentColor = ObsidianBlack
            ),
            shape = RoundedCornerShape(18.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(
                imageVector = if (isOverlayActive) Icons.Default.PictureInPictureAlt else Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (isOverlayActive) {
                    if (isArabic) "إيقاف الساعة العائمة" else "Stop Floating Clock"
                } else {
                    if (isArabic) "تشغيل الساعة العائمة 3D فوق التطبيقات" else "Launch 3D Floating Clock"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Simple Quick Color Palette Bar (Hands & Background Colors)
        SimpleQuickPaletteBar(
            settings = settings,
            onUpdateHandColor = { viewModel.updateHandColor(it) },
            onUpdateBgColor = { viewModel.updateBackgroundColor(it) },
            isArabic = isArabic
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Toggle button to open/close Full Studio Customizer
        OutlinedButton(
            onClick = { showDeepCustomizer = !showDeepCustomizer },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("toggle_customizer_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RoyalGold),
            border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.horizontalGradient(listOf(RoyalGold, GlassBlue)))
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = RoyalGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (showDeepCustomizer) {
                    if (isArabic) "إخفاء استوديو التخصيص المتقدم" else "Hide Advanced Customizer"
                } else {
                    if (isArabic) "استوديو التخصيص المتقدم (عقارب، خلفيات، أنماط)" else "Open Advanced Customizer (Hands, Dial, Glass)"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        AnimatedVisibility(
            visible = showDeepCustomizer,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                FullCustomizationPanel(
                    settings = settings,
                    onSettingsChanged = { viewModel.updateSettings(it) },
                    isArabic = isArabic
                )

                Spacer(modifier = Modifier.height(12.dp))

                ClockStyleSelector(
                    settings = settings,
                    onSettingsChanged = { viewModel.updateSettings(it) },
                    isArabic = isArabic
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
