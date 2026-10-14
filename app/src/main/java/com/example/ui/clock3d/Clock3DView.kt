package com.example.ui.clock3d

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.model.Clock3DSettings
import com.example.model.DialStyle
import com.example.model.HandStyle
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Clock3DView(
    settings: Clock3DSettings,
    hours: Float, // 0..12 float (e.g. 10.5 for 10:30)
    minutes: Float, // 0..60 float
    seconds: Float, // 0..60 float
    modifier: Modifier = Modifier,
    isInteractive: Boolean = true
) {
    // Rotation angles in degrees
    var pitchDeg by remember { mutableFloatStateOf(16f) }
    var rollDeg by remember { mutableFloatStateOf(-14f) }

    // Subtle ambient breathing/floating idle wobble
    val wobbleAnim = remember { Animatable(0f) }
    LaunchedEffect(settings.autoWobble) {
        if (settings.autoWobble) {
            wobbleAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            wobbleAnim.snapTo(0f)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val currentPitch = pitchDeg + if (settings.autoWobble) (wobbleAnim.value * 6f - 3f) else 0f
    val currentRoll = rollDeg + if (settings.autoWobble) (wobbleAnim.value * 4f - 2f) else 0f

    val pitchRad = (currentPitch * PI / 180.0).toFloat()
    val rollRad = (currentRoll * PI / 180.0).toFloat()

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (isInteractive) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            // Dragging X changes roll, Dragging Y changes pitch
                            rollDeg = (rollDeg + dragAmount.x * 0.35f).coerceIn(-65f, 65f)
                            pitchDeg = (pitchDeg - dragAmount.y * 0.35f).coerceIn(-65f, 65f)
                        }
                    }.pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                coroutineScope.launch {
                                    // Reset smoothly to luxury showcase angle
                                    pitchDeg = 16f
                                    rollDeg = -14f
                                }
                            }
                        )
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (minOf(size.width, size.height) / 2f) * 0.78f

            if (radius <= 10f) return@Canvas

            val depth = 32f * settings.depthExtrusion
            val cameraDist = 900f

            // 1. Draw outer floating drop shadow on virtual surface
            draw3DDropShadow(center, radius, pitchRad, rollRad, cameraDist, settings)

            // 2. Draw 3D Extruded Bezel Wall (cylinder rim)
            draw3DBezelExtrusion(center, radius, depth, pitchRad, rollRad, cameraDist, settings)

            // 3. Draw Back Dial Base & Glass Face
            draw3DDialFace(center, radius, pitchRad, rollRad, cameraDist, settings)

            // 4. Draw Dial Indices / Numbers
            draw3DIndices(center, radius, pitchRad, rollRad, cameraDist, settings)

            // 5. Draw 3D Floating Hands with Directional Shadows
            draw3DHands(center, radius, hours, minutes, seconds, pitchRad, rollRad, cameraDist, settings)

            // 6. Draw 3D Center Crown / Jewel Cap
            draw3DCenterHub(center, pitchRad, rollRad, cameraDist, settings)

            // 7. Draw Glass Crystal Reflection & Specular Glare Arc
            draw3DGlassReflection(center, radius, pitchRad, rollRad, cameraDist, settings)
        }
    }
}

private fun DrawScope.draw3DDropShadow(
    center: Offset,
    radius: Float,
    pitchRad: Float,
    rollRad: Float,
    cameraDist: Float,
    settings: Clock3DSettings
) {
    // Project shadow base circle at z = -40f
    val shadowCenter = Point3D(0f, 25f, -45f * settings.depthExtrusion)
        .project(center, pitchRad, rollRad, cameraDist)
    val shadowRadius = radius * 1.05f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.55f),
                Color.Black.copy(alpha = 0.25f),
                Color.Transparent
            ),
            center = shadowCenter,
            radius = shadowRadius
        ),
        radius = shadowRadius,
        center = shadowCenter
    )
}

private fun DrawScope.draw3DBezelExtrusion(
    center: Offset,
    radius: Float,
    depth: Float,
    pitchRad: Float,
    rollRad: Float,
    cameraDist: Float,
    settings: Clock3DSettings
) {
    val segments = 48
    val angleStep = (2 * PI / segments).toFloat()

    val backZ = -depth / 2f
    val frontZ = depth / 2f

    val rimColor = settings.background.rimGlowColor
    val baseDark = settings.background.primaryColor

    for (i in 0 until segments) {
        val a1 = i * angleStep
        val a2 = (i + 1) * angleStep

        val p1Back = Point3D(radius * cos(a1), radius * sin(a1), backZ)
        val p2Back = Point3D(radius * cos(a2), radius * sin(a2), backZ)
        val p1Front = Point3D(radius * cos(a1), radius * sin(a1), frontZ)
        val p2Front = Point3D(radius * cos(a2), radius * sin(a2), frontZ)

        // Calculate normal lighting intensity based on angle to light
        val midAngle = (a1 + a2) / 2f
        val lightNorm = (cos(midAngle - (settings.lightAngleDegrees * PI / 180f).toFloat()) + 1f) / 2f

        val segmentColor = Color(
            red = (baseDark.red * (1f - lightNorm) + rimColor.red * lightNorm * 0.8f).coerceIn(0f, 1f),
            green = (baseDark.green * (1f - lightNorm) + rimColor.green * lightNorm * 0.8f).coerceIn(0f, 1f),
            blue = (baseDark.blue * (1f - lightNorm) + rimColor.blue * lightNorm * 0.8f).coerceIn(0f, 1f),
            alpha = settings.background.opacity
        )

        val pt1B = p1Back.project(center, pitchRad, rollRad, cameraDist)
        val pt2B = p2Back.project(center, pitchRad, rollRad, cameraDist)
        val pt2F = p2Front.project(center, pitchRad, rollRad, cameraDist)
        val pt1F = p1Front.project(center, pitchRad, rollRad, cameraDist)

        val wallPath = Path().apply {
            moveTo(pt1B.x, pt1B.y)
            lineTo(pt2B.x, pt2B.y)
            lineTo(pt2F.x, pt2F.y)
            lineTo(pt1F.x, pt1F.y)
            close()
        }
        drawPath(wallPath, color = segmentColor, style = Fill)
    }

    // Outer Bezel Rim Outline
    draw3DCircle(center, radius, frontZ, pitchRad, rollRad, cameraDist, rimColor, 2.5f)
}

private fun DrawScope.draw3DDialFace(
    center: Offset,
    radius: Float,
    pitchRad: Float,
    rollRad: Float,
    cameraDist: Float,
    settings: Clock3DSettings
) {
    val dialZ = 0f
    val segments = 64
    val dialPath = Path()

    for (i in 0 until segments) {
        val angle = i * (2 * PI / segments).toFloat()
        val p = Point3D(radius * cos(angle), radius * sin(angle), dialZ)
            .project(center, pitchRad, rollRad, cameraDist)
        if (i == 0) dialPath.moveTo(p.x, p.y) else dialPath.lineTo(p.x, p.y)
    }
    dialPath.close()

    // Glass / Dial Face Fill with Radial Gradient
    val dialCenterProj = Point3D(0f, 0f, dialZ).project(center, pitchRad, rollRad, cameraDist)
    val dialBrush = if (settings.background.isGradient) {
        Brush.radialGradient(
            colors = listOf(
                settings.background.secondaryColor.copy(alpha = settings.background.opacity),
                settings.background.primaryColor.copy(alpha = settings.background.opacity)
            ),
            center = dialCenterProj,
            radius = radius * 1.1f
        )
    } else {
        Brush.radialGradient(
            colors = listOf(
                settings.background.primaryColor.copy(alpha = settings.background.opacity),
                settings.background.primaryColor.copy(alpha = settings.background.opacity)
            ),
            center = dialCenterProj,
            radius = radius
        )
    }
    drawPath(dialPath, brush = dialBrush, style = Fill)

    // Concentric Guilloche / Luxury Architectural Rings
    draw3DCircle(center, radius * 0.88f, dialZ + 1f, pitchRad, rollRad, cameraDist, settings.background.rimGlowColor.copy(alpha = 0.25f), 1.2f)
    draw3DCircle(center, radius * 0.55f, dialZ + 1f, pitchRad, rollRad, cameraDist, settings.background.rimGlowColor.copy(alpha = 0.15f), 0.8f)
    draw3DCircle(center, radius * 0.25f, dialZ + 1f, pitchRad, rollRad, cameraDist, settings.background.rimGlowColor.copy(alpha = 0.10f), 0.8f)
}

private fun DrawScope.draw3DIndices(
    center: Offset,
    radius: Float,
    pitchRad: Float,
    rollRad: Float,
    cameraDist: Float,
    settings: Clock3DSettings
) {
    val indexZ = 6f * settings.depthExtrusion
    val handColor = settings.handColors.hourColor
    val romanNumerals = listOf("XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI")
    val arabicNumerals = listOf("١٢", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "١٠", "١١")

    val textPaint = Paint().apply {
        color = android.graphics.Color.argb(
            (handColor.alpha * 255).toInt(),
            (handColor.red * 255).toInt(),
            (handColor.green * 255).toInt(),
            (handColor.blue * 255).toInt()
        )
        isAntiAlias = true
        textSize = radius * 0.13f
        textAlign = Paint.Align.CENTER
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD)
    }

    for (h in 0 until 12) {
        val angle = (h * 30f - 90f) * (PI / 180f).toFloat()
        val cosA = cos(angle)
        val sinA = sin(angle)

        when (settings.dialStyle) {
            DialStyle.ROMAN -> {
                val p = Point3D(radius * 0.76f * cosA, radius * 0.76f * sinA, indexZ)
                    .project(center, pitchRad, rollRad, cameraDist)
                drawContext.canvas.nativeCanvas.drawText(
                    romanNumerals[h],
                    p.x,
                    p.y + (textPaint.textSize / 3f),
                    textPaint
                )
            }
            DialStyle.ARABIC -> {
                val p = Point3D(radius * 0.76f * cosA, radius * 0.76f * sinA, indexZ)
                    .project(center, pitchRad, rollRad, cameraDist)
                drawContext.canvas.nativeCanvas.drawText(
                    arabicNumerals[h],
                    p.x,
                    p.y + (textPaint.textSize / 3f),
                    textPaint
                )
            }
            DialStyle.DOTS -> {
                val isMajor = (h % 3 == 0)
                val dotRadius = if (isMajor) 5.5f else 3f
                val p = Point3D(radius * 0.82f * cosA, radius * 0.82f * sinA, indexZ)
                    .project(center, pitchRad, rollRad, cameraDist)
                drawCircle(color = handColor, radius = dotRadius, center = p)
            }
            DialStyle.MINIMALIST, DialStyle.ARCHITECTURAL_GLASS -> {
                val isMajor = (h % 3 == 0)
                val innerR = if (isMajor) radius * 0.72f else radius * 0.78f
                val outerR = radius * 0.86f
                val strokeW = if (isMajor) 4f else 2f

                // Draw 3D index line
                val pInner = Point3D(innerR * cosA, innerR * sinA, indexZ)
                    .project(center, pitchRad, rollRad, cameraDist)
                val pOuter = Point3D(outerR * cosA, outerR * sinA, indexZ)
                    .project(center, pitchRad, rollRad, cameraDist)

                drawLine(
                    color = if (isMajor) handColor else handColor.copy(alpha = 0.65f),
                    start = pInner,
                    end = pOuter,
                    strokeWidth = strokeW
                )
            }
        }
    }

    // Minute tick dots between hours
    for (m in 0 until 60) {
        if (m % 5 == 0) continue
        val angle = (m * 6f - 90f) * (PI / 180f).toFloat()
        val p = Point3D(radius * 0.85f * cos(angle), radius * 0.85f * sin(angle), 2f)
            .project(center, pitchRad, rollRad, cameraDist)
        drawCircle(color = handColor.copy(alpha = 0.25f), radius = 1.3f, center = p)
    }
}

private fun DrawScope.draw3DHands(
    center: Offset,
    radius: Float,
    hours: Float,
    minutes: Float,
    seconds: Float,
    pitchRad: Float,
    rollRad: Float,
    cameraDist: Float,
    settings: Clock3DSettings
) {
    val hourAngle = ((hours % 12f) * 30f + (minutes / 60f) * 30f - 90f) * (PI / 180f).toFloat()
    val minuteAngle = (minutes * 6f + (seconds / 60f) * 0.1f - 90f) * (PI / 180f).toFloat()
    val secondAngle = (seconds * 6f - 90f) * (PI / 180f).toFloat()

    val hourZ = 12f * settings.depthExtrusion
    val minuteZ = 22f * settings.depthExtrusion
    val secondZ = 32f * settings.depthExtrusion

    // Light offset for shadows
    val lightAngle = settings.lightAngleDegrees * (PI / 180f).toFloat()
    val shadowDist = 12f * settings.depthExtrusion
    val shadowOffset = Offset(cos(lightAngle) * shadowDist, sin(lightAngle) * shadowDist)

    // 1. Draw Hour Hand Drop Shadow & Hand
    drawHandShadow(center, radius * 0.48f, hourAngle, hourZ, shadowOffset, pitchRad, rollRad, cameraDist)
    drawSingle3DHand(
        center = center,
        length = radius * 0.48f,
        width = radius * 0.055f,
        tailLength = radius * 0.10f,
        angle = hourAngle,
        zDepth = hourZ,
        color = settings.handColors.hourColor,
        style = settings.handStyle,
        pitchRad = pitchRad,
        rollRad = rollRad,
        cameraDist = cameraDist
    )

    // 2. Draw Minute Hand Drop Shadow & Hand
    drawHandShadow(center, radius * 0.72f, minuteAngle, minuteZ, shadowOffset, pitchRad, rollRad, cameraDist)
    drawSingle3DHand(
        center = center,
        length = radius * 0.72f,
        width = radius * 0.040f,
        tailLength = radius * 0.12f,
        angle = minuteAngle,
        zDepth = minuteZ,
        color = settings.handColors.minuteColor,
        style = settings.handStyle,
        pitchRad = pitchRad,
        rollRad = rollRad,
        cameraDist = cameraDist
    )

    // 3. Draw Second Hand (if enabled)
    if (settings.showSeconds) {
        val secColor = settings.handColors.secondColor
        val secTailR = radius * 0.16f
        val secHeadR = radius * 0.82f

        val pTail = Point3D(-secTailR * cos(secondAngle), -secTailR * sin(secondAngle), secondZ)
            .project(center, pitchRad, rollRad, cameraDist)
        val pHead = Point3D(secHeadR * cos(secondAngle), secHeadR * sin(secondAngle), secondZ)
            .project(center, pitchRad, rollRad, cameraDist)

        // Drop shadow for second needle
        val pTailS = Point3D(-secTailR * cos(secondAngle) + shadowOffset.x * 0.6f, -secTailR * sin(secondAngle) + shadowOffset.y * 0.6f, 0f)
            .project(center, pitchRad, rollRad, cameraDist)
        val pHeadS = Point3D(secHeadR * cos(secondAngle) + shadowOffset.x * 0.6f, secHeadR * sin(secondAngle) + shadowOffset.y * 0.6f, 0f)
            .project(center, pitchRad, rollRad, cameraDist)
        drawLine(color = Color.Black.copy(alpha = 0.35f), start = pTailS, end = pHeadS, strokeWidth = 2.2f)

        // Second Hand Needle
        drawLine(color = secColor, start = pTail, end = pHead, strokeWidth = 2.2f)

        // Luminous Orb on Second Hand
        val orbR = radius * 0.58f
        val pOrb = Point3D(orbR * cos(secondAngle), orbR * sin(secondAngle), secondZ)
            .project(center, pitchRad, rollRad, cameraDist)
        drawCircle(color = secColor, radius = 4.2f, center = pOrb)
        drawCircle(color = Color.White, radius = 2.0f, center = pOrb)
    }
}

private fun DrawScope.drawSingle3DHand(
    center: Offset,
    length: Float,
    width: Float,
    tailLength: Float,
    angle: Float,
    zDepth: Float,
    color: Color,
    style: HandStyle,
    pitchRad: Float,
    rollRad: Float,
    cameraDist: Float
) {
    val cosA = cos(angle)
    val sinA = sin(angle)
    val normCos = -sinA
    val normSin = cosA

    when (style) {
        HandStyle.SWORD -> {
            // Faceted 3D Diamond Sword Hand: Split into Light and Dark side facets
            val tip = Point3D(length * cosA, length * sinA, zDepth)
            val left = Point3D(-tailLength * 0.3f * cosA + width * normCos, -tailLength * 0.3f * sinA + width * normSin, zDepth)
            val right = Point3D(-tailLength * 0.3f * cosA - width * normCos, -tailLength * 0.3f * sinA - width * normSin, zDepth)
            val tail = Point3D(-tailLength * cosA, -tailLength * sinA, zDepth)
            val centerRidge = Point3D(length * 0.6f * cosA, length * 0.6f * sinA, zDepth + 4f)

            val pTip = tip.project(center, pitchRad, rollRad, cameraDist)
            val pLeft = left.project(center, pitchRad, rollRad, cameraDist)
            val pRight = right.project(center, pitchRad, rollRad, cameraDist)
            val pTail = tail.project(center, pitchRad, rollRad, cameraDist)
            val pRidge = centerRidge.project(center, pitchRad, rollRad, cameraDist)

            // Light side facet
            val lightFacet = Path().apply {
                moveTo(pTail.x, pTail.y)
                lineTo(pLeft.x, pLeft.y)
                lineTo(pTip.x, pTip.y)
                lineTo(pRidge.x, pRidge.y)
                close()
            }
            drawPath(lightFacet, color = color.copy(alpha = 1f), style = Fill)

            // Shaded side facet
            val darkFacet = Path().apply {
                moveTo(pTail.x, pTail.y)
                lineTo(pRight.x, pRight.y)
                lineTo(pTip.x, pTip.y)
                lineTo(pRidge.x, pRidge.y)
                close()
            }
            drawPath(darkFacet, color = color.copy(red = color.red * 0.72f, green = color.green * 0.72f, blue = color.blue * 0.72f), style = Fill)
        }
        HandStyle.BAUHAUS -> {
            // Sleek architectural rectangular baton with bevel
            val p1 = Point3D(length * cosA + (width / 2f) * normCos, length * sinA + (width / 2f) * normSin, zDepth)
                .project(center, pitchRad, rollRad, cameraDist)
            val p2 = Point3D(length * cosA - (width / 2f) * normCos, length * sinA - (width / 2f) * normSin, zDepth)
                .project(center, pitchRad, rollRad, cameraDist)
            val p3 = Point3D(-tailLength * cosA - (width / 2f) * normCos, -tailLength * sinA - (width / 2f) * normSin, zDepth)
                .project(center, pitchRad, rollRad, cameraDist)
            val p4 = Point3D(-tailLength * cosA + (width / 2f) * normCos, -tailLength * sinA + (width / 2f) * normSin, zDepth)
                .project(center, pitchRad, rollRad, cameraDist)

            val baton = Path().apply {
                moveTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                lineTo(p3.x, p3.y)
                lineTo(p4.x, p4.y)
                close()
            }
            drawPath(baton, color = color, style = Fill)
            drawPath(baton, color = Color.White.copy(alpha = 0.4f), style = Stroke(1.5f))
        }
        HandStyle.SKELETON -> {
            // Hollowed precision architecture
            val pHead = Point3D(length * cosA, length * sinA, zDepth)
                .project(center, pitchRad, rollRad, cameraDist)
            val pTail = Point3D(-tailLength * cosA, -tailLength * sinA, zDepth)
                .project(center, pitchRad, rollRad, cameraDist)

            drawLine(color = color, start = pTail, end = pHead, strokeWidth = width * 1.2f)

            // Hollow inner window
            val pMid = Point3D(length * 0.5f * cosA, length * 0.5f * sinA, zDepth)
                .project(center, pitchRad, rollRad, cameraDist)
            drawCircle(color = Color.Black.copy(alpha = 0.85f), radius = width * 0.7f, center = pMid)
            drawCircle(color = color, radius = width * 0.7f, center = pMid, style = Stroke(1.5f))
        }
        HandStyle.NEON_GLOW -> {
            // Glowing vibrant laser bar
            val pHead = Point3D(length * cosA, length * sinA, zDepth)
                .project(center, pitchRad, rollRad, cameraDist)
            val pTail = Point3D(-tailLength * cosA, -tailLength * sinA, zDepth)
                .project(center, pitchRad, rollRad, cameraDist)

            // Neon halo
            drawLine(color = color.copy(alpha = 0.35f), start = pTail, end = pHead, strokeWidth = width * 2.8f)
            // Core
            drawLine(color = color, start = pTail, end = pHead, strokeWidth = width * 1.1f)
            // White hot center
            drawLine(color = Color.White.copy(alpha = 0.85f), start = pTail, end = pHead, strokeWidth = width * 0.4f)
        }
    }
}

private fun DrawScope.drawHandShadow(
    center: Offset,
    length: Float,
    angle: Float,
    zDepth: Float,
    shadowOffset: Offset,
    pitchRad: Float,
    rollRad: Float,
    cameraDist: Float
) {
    val cosA = cos(angle)
    val sinA = sin(angle)
    val pTip = Point3D(length * cosA + shadowOffset.x, length * sinA + shadowOffset.y, 0f)
        .project(center, pitchRad, rollRad, cameraDist)
    val pCenter = Point3D(shadowOffset.x, shadowOffset.y, 0f)
        .project(center, pitchRad, rollRad, cameraDist)

    drawLine(
        color = Color.Black.copy(alpha = 0.32f),
        start = pCenter,
        end = pTip,
        strokeWidth = 5f
    )
}

private fun DrawScope.draw3DCenterHub(
    center: Offset,
    pitchRad: Float,
    rollRad: Float,
    cameraDist: Float,
    settings: Clock3DSettings
) {
    val hubZ = 36f * settings.depthExtrusion
    val pHub = Point3D(0f, 0f, hubZ).project(center, pitchRad, rollRad, cameraDist)

    // Outer Hub Ring
    drawCircle(color = settings.handColors.hourColor, radius = 9f, center = pHub)
    // Jewel / Core Cap
    drawCircle(color = Color(0xFF1E293B), radius = 6f, center = pHub)
    // Highlight glint
    drawCircle(color = Color.White.copy(alpha = 0.9f), radius = 2.5f, center = Offset(pHub.x - 2f, pHub.y - 2f))
}

private fun DrawScope.draw3DGlassReflection(
    center: Offset,
    radius: Float,
    pitchRad: Float,
    rollRad: Float,
    cameraDist: Float,
    settings: Clock3DSettings
) {
    val crystalZ = 40f * settings.depthExtrusion

    // Dynamic specular reflection follows the pitch and roll tilt!
    val glareShiftX = sin(rollRad) * radius * 0.45f
    val glareShiftY = -sin(pitchRad) * radius * 0.45f

    val glareCenter = Point3D(glareShiftX, glareShiftY, crystalZ)
        .project(center, pitchRad, rollRad, cameraDist)

    // Specular diagonal glare ellipse
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.22f),
                Color.White.copy(alpha = 0.08f),
                Color.Transparent
            ),
            center = glareCenter,
            radius = radius * 0.75f
        ),
        radius = radius * 0.75f,
        center = glareCenter
    )

    // Front Crystal Ring Bevel Highlight
    draw3DCircle(center, radius * 0.96f, crystalZ, pitchRad, rollRad, cameraDist, Color.White.copy(alpha = 0.28f), 1.6f)
}

private fun DrawScope.draw3DCircle(
    center: Offset,
    radius: Float,
    zDepth: Float,
    pitchRad: Float,
    rollRad: Float,
    cameraDist: Float,
    color: Color,
    strokeWidth: Float
) {
    val segments = 48
    val path = Path()
    for (i in 0 until segments) {
        val angle = i * (2 * PI / segments).toFloat()
        val p = Point3D(radius * cos(angle), radius * sin(angle), zDepth)
            .project(center, pitchRad, rollRad, cameraDist)
        if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
    }
    path.close()
    drawPath(path, color = color, style = Stroke(strokeWidth))
}
