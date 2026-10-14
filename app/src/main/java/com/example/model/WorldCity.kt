package com.example.model

import java.util.TimeZone

data class WorldCity(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val countryEn: String,
    val countryAr: String,
    val timeZoneId: String
) {
    val timeZone: TimeZone get() = TimeZone.getTimeZone(timeZoneId)
}

object WorldCitiesData {
    val defaultCities = listOf(
        WorldCity("riyadh", "Riyadh", "الرياض", "Saudi Arabia", "المملكة العربية السعودية", "Asia/Riyadh"),
        WorldCity("mecca", "Makkah", "مكة المكرمة", "Saudi Arabia", "المملكة العربية السعودية", "Asia/Riyadh"),
        WorldCity("dubai", "Dubai", "دبي", "United Arab Emirates", "الإمارات العربية المتحدة", "Asia/Dubai"),
        WorldCity("cairo", "Cairo", "القاهرة", "Egypt", "مصر", "Africa/Cairo"),
        WorldCity("london", "London", "لندن", "United Kingdom", "المملكة المتحدة", "Europe/London"),
        WorldCity("new_york", "New York", "نيويورك", "United States", "الولايات المتحدة", "America/New_York"),
        WorldCity("tokyo", "Tokyo", "طوكيو", "Japan", "اليابان", "Asia/Tokyo")
    )
}
