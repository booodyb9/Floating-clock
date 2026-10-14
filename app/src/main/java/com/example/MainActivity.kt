package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.FloatingManager
import com.example.ui.components.DraggableFloatingWidget
import com.example.ui.screens.BedsideWorldScreen
import com.example.ui.screens.ClockStudioScreen
import com.example.ui.screens.FloatingSettingsScreen
import com.example.ui.screens.StopwatchTimerScreen
import com.example.ui.theme.GlassBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SurfaceCard
import com.example.viewmodel.ClockViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ClockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: ClockViewModel) {
    var selectedScreenIndex by remember { mutableIntStateOf(0) }
    val isArabic by viewModel.isArabic.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val hours by viewModel.hours.collectAsState()
    val minutes by viewModel.minutes.collectAsState()
    val seconds by viewModel.seconds.collectAsState()

    val navItems = listOf(
        Triple(if (isArabic) "الاستوديو 3D" else "3D Studio", Icons.Default.ViewInAr, "nav_studio"),
        Triple(if (isArabic) "النافذة العائمة" else "Floating", Icons.Default.PictureInPictureAlt, "nav_floating"),
        Triple(if (isArabic) "المؤقت" else "Timer", Icons.Default.Timer, "nav_timer"),
        Triple(if (isArabic) "الليلي والعالمي" else "World", Icons.Default.Bedtime, "nav_bedside")
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        topBar = {
            TopAppBarLuxury(
                isArabic = isArabic,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onQuickToggleFloating = { viewModel.toggleFloatingOverlay() },
                isFloatingActive = FloatingManager.isFloatingActive || FloatingManager.isInAppFloatingActive
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = SurfaceCard.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedScreenIndex == index,
                        onClick = { selectedScreenIndex = index },
                        icon = {
                            Icon(
                                imageVector = item.second,
                                contentDescription = item.first,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.first,
                                fontSize = 11.sp,
                                fontWeight = if (selectedScreenIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianBlack,
                            selectedTextColor = RoyalGold,
                            indicatorColor = RoyalGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag(item.third)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ObsidianBlack)
        ) {
            when (selectedScreenIndex) {
                0 -> ClockStudioScreen(viewModel = viewModel)
                1 -> FloatingSettingsScreen(viewModel = viewModel)
                2 -> StopwatchTimerScreen(viewModel = viewModel)
                3 -> BedsideWorldScreen(viewModel = viewModel)
            }

            // In-App Draggable Floating Clock Widget Overlay
            DraggableFloatingWidget(
                settings = settings,
                hours = hours,
                minutes = minutes,
                seconds = seconds,
                isVisible = FloatingManager.isInAppFloatingActive,
                onClose = { viewModel.closeInAppFloating() },
                isArabic = isArabic
            )
        }
    }
}

@Composable
private fun TopAppBarLuxury(
    isArabic: Boolean,
    onToggleLanguage: () -> Unit,
    onQuickToggleFloating: () -> Unit,
    isFloatingActive: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars),
        color = ObsidianBlack.copy(alpha = 0.95f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Branding & Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(RoyalGold, GlassBlue))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Logo",
                        tint = ObsidianBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isArabic) "ساعة 3D العائمة" else "3D Floating Clock",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isArabic) "استوديو التخصيص المعماري الفاخر" else "Architectural Luxury Studio",
                        fontSize = 10.sp,
                        color = RoyalGold
                    )
                }
            }

            // Quick Actions: Quick Floating Toggle + Language Switcher
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Floating status badge button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isFloatingActive) GlassBlue.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f)
                        )
                        .border(
                            1.dp,
                            if (isFloatingActive) GlassBlue else Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isFloatingActive) GlassBlue else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isFloatingActive) {
                                if (isArabic) "عائمة نشطة" else "Floating"
                            } else {
                                if (isArabic) "عائمة" else "Float"
                            },
                            fontSize = 10.sp,
                            color = if (isFloatingActive) GlassBlue else Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Language toggle button
                IconButton(
                    onClick = onToggleLanguage,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .testTag("language_toggle_button")
                ) {
                    Text(
                        text = if (isArabic) "EN" else "عربي",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalGold
                    )
                }
            }
        }
    }
}
