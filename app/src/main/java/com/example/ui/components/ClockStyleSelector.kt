package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Clock3DSettings
import com.example.model.DialStyle
import com.example.model.HandStyle
import com.example.ui.theme.GlassBlue
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SurfaceCard

@Composable
fun ClockStyleSelector(
    settings: Clock3DSettings,
    onSettingsChanged: (Clock3DSettings) -> Unit,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("clock_style_selector"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.90f)),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.12f)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Section 1: Hand Architecture Style
            Text(
                text = if (isArabic) "تصميم العقارب المعماري" else "Hand Architectural Style",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalGold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HandStyle.values().forEach { style ->
                    val isSelected = settings.handStyle == style
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) RoyalGold else Color.White.copy(alpha = 0.07f))
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSettingsChanged(settings.copy(handStyle = style)) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isArabic) style.titleAr else style.titleEn,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ObsidianBlack else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Dial Typography & Markers Style
            Text(
                text = if (isArabic) "نمط قرص الساعة والأرقام" else "Dial Markers & Numerals",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GlassBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DialStyle.values().forEach { style ->
                    val isSelected = settings.dialStyle == style
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) GlassBlue else Color.White.copy(alpha = 0.07f))
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSettingsChanged(settings.copy(dialStyle = style)) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isArabic) style.titleAr else style.titleEn,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ObsidianBlack else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: 3D Depth Extrusion Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = null,
                        tint = RoyalGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "عمق وسُمك المجسم 3D" else "3D Depth Extrusion",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
                Text(
                    text = "${(settings.depthExtrusion * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = RoyalGold,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = settings.depthExtrusion,
                onValueChange = { onSettingsChanged(settings.copy(depthExtrusion = it)) },
                valueRange = 0.4f..1.8f,
                colors = SliderDefaults.colors(
                    thumbColor = RoyalGold,
                    activeTrackColor = RoyalGold,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Section 4: Light Reflection Angle Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LightMode,
                        contentDescription = null,
                        tint = GlassBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "زاوية انعكاس الضوء على الزجاج" else "Glass Specular Light Angle",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
                Text(
                    text = "${settings.lightAngleDegrees.toInt()}°",
                    fontSize = 12.sp,
                    color = GlassBlue,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = settings.lightAngleDegrees,
                onValueChange = { onSettingsChanged(settings.copy(lightAngleDegrees = it)) },
                valueRange = 0f..360f,
                colors = SliderDefaults.colors(
                    thumbColor = GlassBlue,
                    activeTrackColor = GlassBlue,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Toggles: Auto wobble / Smooth Sweep Seconds / Show Seconds
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isArabic) "حركة الطفو ثلاثية الأبعاد (تأرجح هادئ)" else "3D Floating Ambient Breathing",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = if (isArabic) "محاكاة حركة الجيروسكوب التفاعلية" else "Gentle floating perspective shift",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
                Switch(
                    checked = settings.autoWobble,
                    onCheckedChange = { onSettingsChanged(settings.copy(autoWobble = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ObsidianBlack,
                        checkedTrackColor = RoyalGold
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isArabic) "عقرب الثواني الانسيابي (Sweep)" else "Smooth Sweep Second Hand",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = if (isArabic) "حركة ميكانيكية انسيابية فائقة الدقة" else "Ultra-fluid mechanical continuous tick",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
                Switch(
                    checked = settings.smoothSweepSeconds,
                    onCheckedChange = { onSettingsChanged(settings.copy(smoothSweepSeconds = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ObsidianBlack,
                        checkedTrackColor = GlassBlue
                    )
                )
            }
        }
    }
}
