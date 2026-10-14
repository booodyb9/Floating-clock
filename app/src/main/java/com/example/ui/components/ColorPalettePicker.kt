package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundConfig
import com.example.model.Clock3DSettings
import com.example.model.ClockColorPalettes
import com.example.model.HandColorConfig
import com.example.model.PaletteColor
import com.example.ui.theme.GlassBlue
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SurfaceCard

enum class HandTarget {
    ALL, HOUR, MINUTE, SECOND
}

@Composable
fun SimpleQuickPaletteBar(
    settings: Clock3DSettings,
    onUpdateHandColor: (Color) -> Unit,
    onUpdateBgColor: (Color) -> Unit,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_palette_bar"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.85f)),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(RoyalGold.copy(alpha = 0.35f)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Palette",
                        tint = RoyalGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "لوحة الألوان السريعة" else "Quick Color Palette",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = if (isArabic) "اختر لونك المفضل" else "Tap to apply",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Hand Colors Row
            Text(
                text = if (isArabic) "لون العقارب:" else "Hands Color:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = RoyalGold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ClockColorPalettes.handPalettes.forEach { paletteItem ->
                    ColorCircleButton(
                        color = paletteItem.color,
                        name = if (isArabic) paletteItem.nameAr else paletteItem.nameEn,
                        isSelected = settings.handColors.hourColor == paletteItem.color,
                        onClick = { onUpdateHandColor(paletteItem.color) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Background Colors Row
            Text(
                text = if (isArabic) "لون الخلفية:" else "Background Color:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = GlassBlue
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ClockColorPalettes.backgroundPalettes.forEach { paletteItem ->
                    ColorCircleButton(
                        color = paletteItem.color,
                        name = if (isArabic) paletteItem.nameAr else paletteItem.nameEn,
                        isSelected = settings.background.primaryColor == paletteItem.color,
                        onClick = { onUpdateBgColor(paletteItem.color) }
                    )
                }
            }
        }
    }
}

@Composable
fun FullCustomizationPanel(
    settings: Clock3DSettings,
    onSettingsChanged: (Clock3DSettings) -> Unit,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var handTarget by remember { mutableStateOf(HandTarget.ALL) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("full_customization_panel"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.92f)),
        shape = RoundedCornerShape(24.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.15f)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header tabs: Hands / Background / Themes / 3D Light
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = RoyalGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = RoyalGold,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = if (isArabic) "العقارب" else "Hands",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = if (isArabic) "الخلفية" else "Background",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = if (isArabic) "أنماط فاخرة" else "Presets",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // Hand Colors Sub-Selection
                    Text(
                        text = if (isArabic) "اختر العقرب المراد تلوينه:" else "Target Hand to Color:",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = handTarget == HandTarget.ALL,
                            onClick = { handTarget = HandTarget.ALL },
                            label = { Text(if (isArabic) "الكل معاً" else "All Hands", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalGold,
                                selectedLabelColor = ObsidianBlack
                            )
                        )
                        FilterChip(
                            selected = handTarget == HandTarget.HOUR,
                            onClick = { handTarget = HandTarget.HOUR },
                            label = { Text(if (isArabic) "الساعات" else "Hour", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalGold,
                                selectedLabelColor = ObsidianBlack
                            )
                        )
                        FilterChip(
                            selected = handTarget == HandTarget.MINUTE,
                            onClick = { handTarget = HandTarget.MINUTE },
                            label = { Text(if (isArabic) "الدقائق" else "Minute", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalGold,
                                selectedLabelColor = ObsidianBlack
                            )
                        )
                        FilterChip(
                            selected = handTarget == HandTarget.SECOND,
                            onClick = { handTarget = HandTarget.SECOND },
                            label = { Text(if (isArabic) "الثواني" else "Second", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GlassBlue,
                                selectedLabelColor = ObsidianBlack
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isArabic) "اختر اللون:" else "Select Color:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Swatches Grid
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val chunks = ClockColorPalettes.handPalettes.chunked(5)
                        chunks.forEach { rowList ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowList.forEach { p ->
                                    val isCurrent = when (handTarget) {
                                        HandTarget.ALL -> settings.handColors.hourColor == p.color
                                        HandTarget.HOUR -> settings.handColors.hourColor == p.color
                                        HandTarget.MINUTE -> settings.handColors.minuteColor == p.color
                                        HandTarget.SECOND -> settings.handColors.secondColor == p.color
                                    }
                                    ColorCircleButton(
                                        color = p.color,
                                        name = if (isArabic) p.nameAr else p.nameEn,
                                        isSelected = isCurrent,
                                        size = 36.dp,
                                        onClick = {
                                            val newHandColors = when (handTarget) {
                                                HandTarget.ALL -> settings.handColors.copy(
                                                    hourColor = p.color,
                                                    minuteColor = p.color,
                                                    secondColor = p.color
                                                )
                                                HandTarget.HOUR -> settings.handColors.copy(hourColor = p.color)
                                                HandTarget.MINUTE -> settings.handColors.copy(minuteColor = p.color)
                                                HandTarget.SECOND -> settings.handColors.copy(secondColor = p.color)
                                            }
                                            onSettingsChanged(settings.copy(handColors = newHandColors))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Background & Glass Settings
                    Text(
                        text = if (isArabic) "لون الخلفية الأساسي:" else "Primary Background Color:",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ClockColorPalettes.backgroundPalettes.forEach { p ->
                            ColorCircleButton(
                                color = p.color,
                                name = if (isArabic) p.nameAr else p.nameEn,
                                isSelected = settings.background.primaryColor == p.color,
                                size = 36.dp,
                                onClick = {
                                    val newBg = settings.background.copy(primaryColor = p.color)
                                    onSettingsChanged(settings.copy(background = newBg))
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary Gradient Color
                    Text(
                        text = if (isArabic) "لون التدرج الإضافي:" else "Secondary Gradient Tint:",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ClockColorPalettes.handPalettes.forEach { p ->
                            ColorCircleButton(
                                color = p.color,
                                name = if (isArabic) p.nameAr else p.nameEn,
                                isSelected = settings.background.secondaryColor == p.color,
                                size = 32.dp,
                                onClick = {
                                    val newBg = settings.background.copy(secondaryColor = p.color)
                                    onSettingsChanged(settings.copy(background = newBg))
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Glass Opacity Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "شفافية الزجاج المعماري" else "Glass Transparency",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${(settings.background.opacity * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = GlassBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = settings.background.opacity,
                        onValueChange = { newOpacity ->
                            val newBg = settings.background.copy(opacity = newOpacity)
                            onSettingsChanged(settings.copy(background = newBg))
                        },
                        valueRange = 0.2f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = GlassBlue,
                            activeTrackColor = GlassBlue,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
                2 -> {
                    // Luxury Ready-to-use Presets
                    Text(
                        text = if (isArabic) "اختر طابعاً جاهزاً بلمسة واحدة:" else "One-tap Luxury Themes:",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val presets = listOf(
                        Triple(
                            if (isArabic) "ذهب الرياض الملكي" else "Royal Riyadh Gold",
                            HandColorConfig(
                                hourColor = Color(0xFFD4AF37),
                                minuteColor = Color(0xFFD4AF37),
                                secondColor = Color(0xFF00F2FE)
                            ),
                            BackgroundConfig(
                                primaryColor = Color(0xFF070A12),
                                secondaryColor = Color(0xFF1E152A),
                                rimGlowColor = Color(0xFFD4AF37).copy(alpha = 0.6f)
                            )
                        ),
                        Triple(
                            if (isArabic) "أزرق الزجاج البلوري" else "Glass Crystal Blue",
                            HandColorConfig(
                                hourColor = Color(0xFF00F2FE),
                                minuteColor = Color(0xFF00F2FE),
                                secondColor = Color(0xFFFFFFFF)
                            ),
                            BackgroundConfig(
                                primaryColor = Color(0xFF071A2C),
                                secondaryColor = Color(0xFF0F3254),
                                rimGlowColor = Color(0xFF00F2FE).copy(alpha = 0.6f)
                            )
                        ),
                        Triple(
                            if (isArabic) "الزمرد المعماري الفاخر" else "Architectural Emerald",
                            HandColorConfig(
                                hourColor = Color(0xFF10B981),
                                minuteColor = Color(0xFF10B981),
                                secondColor = Color(0xFFD4AF37)
                            ),
                            BackgroundConfig(
                                primaryColor = Color(0xFF041812),
                                secondaryColor = Color(0xFF082D22),
                                rimGlowColor = Color(0xFF10B981).copy(alpha = 0.6f)
                            )
                        ),
                        Triple(
                            if (isArabic) "الذهب الوردي والمخمل" else "Rose Gold & Velvet",
                            HandColorConfig(
                                hourColor = Color(0xFFE08D9D),
                                minuteColor = Color(0xFFE08D9D),
                                secondColor = Color(0xFFF3E5AB)
                            ),
                            BackgroundConfig(
                                primaryColor = Color(0xFF1B0E20),
                                secondaryColor = Color(0xFF2E1736),
                                rimGlowColor = Color(0xFFE08D9D).copy(alpha = 0.6f)
                            )
                        ),
                        Triple(
                            if (isArabic) "تيتانيوم وفضة نقية" else "Pure Silver Titanium",
                            HandColorConfig(
                                hourColor = Color(0xFFE2E8F0),
                                minuteColor = Color(0xFFE2E8F0),
                                secondColor = Color(0xFFF43F5E)
                            ),
                            BackgroundConfig(
                                primaryColor = Color(0xFF161E2E),
                                secondaryColor = Color(0xFF23314B),
                                rimGlowColor = Color(0xFFE2E8F0).copy(alpha = 0.6f)
                            )
                        )
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.forEach { (name, hands, bg) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.06f))
                                    .clickable {
                                        onSettingsChanged(
                                            settings.copy(
                                                handColors = hands,
                                                background = bg
                                            )
                                        )
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Mini color indicators
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(hands.hourColor)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(bg.primaryColor)
                                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (settings.handColors.hourColor == hands.hourColor) RoyalGold else Color.Transparent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorCircleButton(
    color: Color,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 34.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.35f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = if (color == Color.White || color == RoyalGold) Color.Black else Color.White,
                    modifier = Modifier.size(size * 0.55f)
                )
            }
        }
    }
}
