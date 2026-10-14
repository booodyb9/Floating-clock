package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.FloatingManager
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.GlassBlue
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SurfaceCard
import com.example.viewmodel.ClockViewModel

@Composable
fun FloatingSettingsScreen(
    viewModel: ClockViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic by viewModel.isArabic.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var hasOverlayPermission by remember { mutableStateOf(FloatingManager.canDrawOverlays(context)) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("floating_settings_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.90f)),
            shape = RoundedCornerShape(22.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                    if (hasOverlayPermission) EmeraldGlow.copy(alpha = 0.5f) else RoyalGold.copy(alpha = 0.5f)
                )
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (hasOverlayPermission) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (hasOverlayPermission) EmeraldGlow else RoyalGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isArabic) "إذن الظهور فوق التطبيقات (Overlay)" else "Draw Over Apps Permission",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (hasOverlayPermission) {
                                if (isArabic) "مُفعل وجاهز للعمل فوق أي تطبيق" else "Permission Granted - Ready to float"
                            } else {
                                if (isArabic) "مطلوب لتشغيل الساعة فوق الألعاب والشاشة الرئيسية" else "Required to display clock over other apps"
                            },
                            fontSize = 12.sp,
                            color = if (hasOverlayPermission) EmeraldGlow else Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                if (!hasOverlayPermission) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            FloatingManager.requestOverlayPermission(context)
                            hasOverlayPermission = FloatingManager.canDrawOverlays(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("request_permission_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "منح إذن الظهور فوق التطبيقات" else "Grant Overlay Permission",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // In-App Draggable Floating Test Mode
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.85f)),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PictureInPictureAlt,
                        contentDescription = null,
                        tint = GlassBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isArabic) "ساعة 3D العائمة التفاعلية داخل التطبيق" else "Interactive In-App 3D Float Mode",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isArabic) "نافذة عائمة قابلة للسحب والتصغير داخل الشاشة" else "Draggable floating widget with resize & minimize",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        FloatingManager.isInAppFloatingActive = !FloatingManager.isInAppFloatingActive
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (FloatingManager.isInAppFloatingActive) GlassBlue else RoyalGold,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (FloatingManager.isInAppFloatingActive) {
                            if (isArabic) "إخفاء النافذة العائمة" else "Hide In-App Float Widget"
                        } else {
                            if (isArabic) "إظهار الساعة العائمة الآن (تجربة سريعة)" else "Show In-App Floating Widget Now"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Floating Features & Gestures Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.85f)),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (isArabic) "مميزات وحركات الساعة العائمة" else "Floating Clock Gestures & Features",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalGold
                )
                Spacer(modifier = Modifier.height(12.dp))

                FloatingFeatureRow(
                    icon = Icons.Default.TouchApp,
                    title = if (isArabic) "سحب حر باللمس" else "Drag Anywhere",
                    description = if (isArabic) "يمكنك تحريك الساعة إلى أي زاوية من الشاشة بسهولة" else "Move the floating dial to any position on your screen",
                    color = RoyalGold
                )

                Spacer(modifier = Modifier.height(10.dp))

                FloatingFeatureRow(
                    icon = Icons.Default.Layers,
                    title = if (isArabic) "تصغير وتكبير بضغطة زر" else "Tap to Minimize",
                    description = if (isArabic) "تحويل الساعة إلى فقاعة زجاجية صغيرة لتوفير مساحة الشاشة" else "Collapse into a compact floating crystal bubble",
                    color = GlassBlue
                )

                Spacer(modifier = Modifier.height(10.dp))

                FloatingFeatureRow(
                    icon = Icons.Default.OpenInBrowser,
                    title = if (isArabic) "العودة للتطبيق فوراً" else "Instant Access",
                    description = if (isArabic) "انقر على الساعة العائمة للعودة إلى استوديو التخصيص" else "Tap the floating window to reopen the studio anytime",
                    color = EmeraldGlow
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun FloatingFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(text = description, fontSize = 11.sp, color = Color.White.copy(alpha = 0.65f))
        }
    }
}
