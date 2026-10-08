package com.earthquakealert.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.earthquakealert.app.domain.model.Earthquake
import java.time.Instant
import java.time.format.DateTimeParseException

data class BmkgAutoResponseDto(
    @SerializedName("Infogempa")
    val infoGempa: BmkgAutoInfoGempaDto? = null
)

data class BmkgAutoInfoGempaDto(
    @SerializedName("gempa")
    val gempa: BmkgEarthquakeDto? = null
)

data class BmkgListResponseDto(
    @SerializedName("Infogempa")
    val infoGempa: BmkgListInfoGempaDto? = null
)

data class BmkgListInfoGempaDto(
    @SerializedName("gempa")
    val gempaList: List<BmkgEarthquakeDto>? = null
)

data class BmkgEarthquakeDto(
    @SerializedName("Tanggal")
    val tanggal: String? = null,

    @SerializedName("Jam")
    val jam: String? = null,

    @SerializedName("DateTime")
    val dateTime: String? = null,

    @SerializedName("Coordinates")
    val coordinates: String? = null,

    @SerializedName("Lintang")
    val lintang: String? = null,

    @SerializedName("Bujur")
    val bujur: String? = null,

    @SerializedName("Magnitude")
    val magnitude: String? = null,

    @SerializedName("Kedalaman")
    val kedalaman: String? = null,

    @SerializedName("Wilayah")
    val wilayah: String? = null,

    @SerializedName("Potensi")
    val potensi: String? = null,

    @SerializedName("Dirasakan")
    val dirasakan: String? = null,

    @SerializedName("Shakemap")
    val shakemap: String? = null
)

fun BmkgEarthquakeDto.toDomain(): Earthquake? {
    val mag = magnitude?.toDoubleOrNull() ?: return null

    // Parse coordinates
    val (lat, lon) = parseCoordinates(coordinates, lintang, bujur) ?: return null

    // Parse depth in km
    val depth = kedalaman
        ?.replace("km", "", ignoreCase = true)
        ?.trim()
        ?.toDoubleOrNull()

    // Parse timestamp
    val timestamp = parseTimestamp(dateTime)

    // Generate deterministic stable event ID
    val eventId = when {
        !dateTime.isNullOrBlank() && !coordinates.isNullOrBlank() -> "${dateTime}_${coordinates}".trim()
        !dateTime.isNullOrBlank() -> "${dateTime}_${lat}_${lon}".trim()
        else -> "${tanggal}_${jam}_${lat}_${lon}".trim()
    }

    val hasTsunamiPotential = potensi?.let {
        it.contains("tsunami", ignoreCase = true) && !it.contains("tidak", ignoreCase = true)
    }

    return Earthquake(
        id = eventId,
        timestampMillis = timestamp,
        latitude = lat,
        longitude = lon,
        magnitude = mag,
        depthKm = depth,
        region = wilayah?.trim() ?: "Unknown region",
        tsunamiPotential = hasTsunamiPotential,
        mmi = dirasakan?.trim(),
        source = "BMKG"
    )
}

internal fun parseCoordinates(
    coordinates: String?,
    lintang: String?,
    bujur: String?
): Pair<Double, Double>? {
    if (!coordinates.isNullOrBlank()) {
        val parts = coordinates.split(",")
        if (parts.size == 2) {
            val lat = parts[0].trim().toDoubleOrNull()
            val lon = parts[1].trim().toDoubleOrNull()
            if (lat != null && lon != null) {
                return Pair(lat, lon)
            }
        }
    }

    val lat = parseCoordinateComponent(lintang, isLatitude = true)
    val lon = parseCoordinateComponent(bujur, isLatitude = false)
    if (lat != null && lon != null) {
        return Pair(lat, lon)
    }

    return null
}

private fun parseCoordinateComponent(text: String?, isLatitude: Boolean): Double? {
    if (text.isNullOrBlank()) return null
    val upper = text.uppercase().trim()
    val number = upper.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: return null

    return if (isLatitude) {
        if (upper.contains("LS") || upper.contains("S")) -number else number
    } else {
        if (upper.contains("BB") || upper.contains("W")) -number else number
    }
}

internal fun parseTimestamp(dateTime: String?): Long {
    if (!dateTime.isNullOrBlank()) {
        try {
            return Instant.parse(dateTime.trim()).toEpochMilli()
        } catch (_: DateTimeParseException) {
            // fallback
        }
    }
    return System.currentTimeMillis()
}
