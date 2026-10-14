package com.example.ui.clock3d

import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.sin

/**
 * 3D vector and perspective projection utilities for Canvas rendering
 */
data class Point3D(val x: Float, val y: Float, val z: Float) {
    fun rotateX(radians: Float): Point3D {
        val cosA = cos(radians)
        val sinA = sin(radians)
        val y1 = y * cosA - z * sinA
        val z1 = y * sinA + z * cosA
        return Point3D(x, y1, z1)
    }

    fun rotateY(radians: Float): Point3D {
        val cosB = cos(radians)
        val sinB = sin(radians)
        val x1 = x * cosB + z * sinB
        val z1 = -x * sinB + z * cosB
        return Point3D(x1, y, z1)
    }

    fun rotateZ(radians: Float): Point3D {
        val cosG = cos(radians)
        val sinG = sin(radians)
        val x1 = x * cosG - y * sinG
        val y1 = x * sinG + y * cosG
        return Point3D(x1, y1, z)
    }

    fun project(
        center: Offset,
        pitchRad: Float,
        rollRad: Float,
        cameraDistance: Float = 1000f
    ): Offset {
        // Rotate X (pitch) then Y (roll)
        val p1 = this.rotateX(pitchRad)
        val p2 = p1.rotateY(rollRad)

        val factor = cameraDistance / (cameraDistance - p2.z)
        return Offset(
            x = center.x + p2.x * factor,
            y = center.y + p2.y * factor
        )
    }

    fun transformedZ(pitchRad: Float, rollRad: Float): Float {
        val p1 = this.rotateX(pitchRad)
        val p2 = p1.rotateY(rollRad)
        return p2.z
    }
}
