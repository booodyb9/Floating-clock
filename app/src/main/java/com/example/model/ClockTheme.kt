package com.example.model

import androidx.compose.ui.graphics.Color

data class PaletteColor(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val color: Color
)

object ClockColorPalettes {
    val handPalettes = listOf(
        PaletteColor("royal_gold", "Royal Gold", "ذهبي ملكي", Color(0xFFD4AF37)),
        PaletteColor("glass_cyan", "Glass Cyan", "أزرق بلوري", Color(0xFF00F2FE)),
        PaletteColor("emerald_green", "Emerald Green", "زمردي فاخر", Color(0xFF10B981)),
        PaletteColor("rose_gold", "Rose Gold", "ذهب وردي", Color(0xFFE08D9D)),
        PaletteColor("cyber_red", "Cyber Scarlet", "أحمر قرمزي", Color(0xFFF43F5E)),
        PaletteColor("chrome_silver", "Chrome Silver", "فضي كروم", Color(0xFFE2E8F0)),
        PaletteColor("ice_white", "Pure Ice", "أبيض ناصع", Color(0xFFFFFFFF)),
        PaletteColor("violet_amber", "Cosmic Violet", "بنفسجي كوني", Color(0xFFA855F7)),
        PaletteColor("sunset_bronze", "Sunset Bronze", "برونزي دافئ", Color(0xFFF59E0B)),
        PaletteColor("electric_lime", "Electric Lime", "أخضر نيون", Color(0xFF84CC16))
    )

    val backgroundPalettes = listOf(
        PaletteColor("obsidian_void", "Obsidian Black", "أسود بركاني", Color(0xFF070A12)),
        PaletteColor("midnight_navy", "Midnight Navy", "كحلي ملكي", Color(0xFF0B132B)),
        PaletteColor("smoked_glass", "Smoked Glass", "رمادي بلوري", Color(0xFF1E2433)),
        PaletteColor("emerald_aurora", "Deep Emerald", "زمرد ليلي", Color(0xFF051B14)),
        PaletteColor("royal_velvet", "Royal Velvet", "مخملي ملكي", Color(0xFF1B0E29)),
        PaletteColor("cyber_blue", "Cyber Abyss", "أزرق سيبراني", Color(0xFF061A28)),
        PaletteColor("carbon_graphite", "Carbon Graphite", "كاربون جرافيت", Color(0xFF18181B)),
        PaletteColor("frosted_titanium", "Titanium Glass", "تيتانيوم ثلجي", Color(0xFF272F3D))
    )
}

enum class HandStyle(val titleEn: String, val titleAr: String) {
    SWORD("Architectural Sword", "سيف معماري فاخر"),
    BAUHAUS("Modern Minimal", "مينيمال حديث"),
    SKELETON("Skeleton Precision", "هيكلي مفرغ"),
    NEON_GLOW("Luminous Beam", "شعاع نيون مضيء")
}

enum class DialStyle(val titleEn: String, val titleAr: String) {
    ARCHITECTURAL_GLASS("Architectural Glass", "زجاج معماري"),
    MINIMALIST("Minimal Indices", "مؤشرات بسيطة"),
    ROMAN("Imperial Roman", "أرقام رومانية"),
    ARABIC("Arabic Modern", "أرقام عربية"),
    DOTS("Star Dots", "نقاط نجومية")
}

data class HandColorConfig(
    val hourColor: Color = Color(0xFFD4AF37),
    val minuteColor: Color = Color(0xFFD4AF37),
    val secondColor: Color = Color(0xFF00F2FE),
    val linkColors: Boolean = false
)

data class BackgroundConfig(
    val primaryColor: Color = Color(0xFF070A12),
    val secondaryColor: Color = Color(0xFF111827),
    val isGradient: Boolean = true,
    val glassBlur: Float = 0.85f,
    val opacity: Float = 0.95f,
    val rimGlowColor: Color = Color(0xFFD4AF37).copy(alpha = 0.5f)
)

data class Clock3DSettings(
    val depthExtrusion: Float = 1.0f, // 0.5f .. 2.0f
    val lightAngleDegrees: Float = 45f,
    val autoWobble: Boolean = true,
    val showSeconds: Boolean = true,
    val smoothSweepSeconds: Boolean = true,
    val is24Hour: Boolean = false,
    val showDate: Boolean = true,
    val showHijriDate: Boolean = true,
    val handStyle: HandStyle = HandStyle.SWORD,
    val dialStyle: DialStyle = DialStyle.ARCHITECTURAL_GLASS,
    val handColors: HandColorConfig = HandColorConfig(),
    val background: BackgroundConfig = BackgroundConfig()
)
