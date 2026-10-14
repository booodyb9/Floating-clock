package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.model.Clock3DSettings

object FloatingManager {
    var isFloatingActive by mutableStateOf(false)
    var isInAppFloatingActive by mutableStateOf(false)
    var currentSettings by mutableStateOf(Clock3DSettings())

    fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    fun requestOverlayPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun startFloatingService(context: Context, settings: Clock3DSettings) {
        currentSettings = settings
        if (canDrawOverlays(context)) {
            val intent = Intent(context, FloatingClockService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            isFloatingActive = true
        } else {
            // Fallback immediately to interactive In-App Floating Mode!
            isInAppFloatingActive = true
        }
    }

    fun stopFloatingService(context: Context) {
        val intent = Intent(context, FloatingClockService::class.java)
        context.stopService(intent)
        isFloatingActive = false
        isInAppFloatingActive = false
    }
}
