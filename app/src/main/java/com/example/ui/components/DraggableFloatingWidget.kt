package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ZoomInMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Clock3DSettings
import com.example.ui.clock3d.Clock3DView
import com.example.ui.theme.GlassBlue
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoyalGold
import kotlin.math.roundToInt

@Composable
fun DraggableFloatingWidget(
    settings: Clock3DSettings,
    hours: Float,
    minutes: Float,
    seconds: Float,
    isVisible: Boolean,
    onClose: () -> Unit,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    var offsetX by remember { mutableFloatStateOf(40f) }
    var offsetY by remember { mutableFloatStateOf(160f) }
    var isMinimized by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .testTag("in_app_floating_widget")
    ) {
        if (isMinimized) {
            // Minimized Floating Glass Bubble
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(ObsidianBlack.copy(alpha = 0.92f))
                    .border(2.dp, RoyalGold, CircleShape)
                    .clickable { isMinimized = false }
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Clock3DView(
                    settings = settings,
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    isInteractive = false
                )
            }
        } else {
            // Expanded Floating Glass Card with 3D Clock and Quick Window Controls
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .shadow(20.dp, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .background(settings.background.primaryColor.copy(alpha = 0.92f))
                    .border(2.dp, settings.background.rimGlowColor, RoundedCornerShape(28.dp))
            ) {
                // The 3D Clock View
                Clock3DView(
                    settings = settings,
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    isInteractive = false
                )

                // Top Controls: Minimize / Close
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isMinimized = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomInMap,
                            contentDescription = "Minimize",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Bottom badge: "3D FLOATING"
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isArabic) "ساعة 3D عائمة" else "3D Floating",
                        fontSize = 9.sp,
                        color = RoyalGold
                    )
                }
            }
        }
    }
}
