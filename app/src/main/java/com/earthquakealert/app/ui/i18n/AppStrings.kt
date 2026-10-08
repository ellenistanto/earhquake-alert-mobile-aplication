package com.earthquakealert.app.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.earthquakealert.app.domain.model.AppLanguage
import java.util.Locale

interface Strings {
    // Navigation
    val navHome: String
    val navHistory: String
    val navSettings: String

    // Home Screen
    val appTitle: String
    val latestEarthquakeSection: String
    val shakeMap: String
    val share: String
    val magnitude: String
    val depth: String
    val coordinates: String
    val feltEarthquake: String
    val tsunamiWarning: String
    val significantEarthquake: String
    val recentEarthquake: String
    val time: String
    val earthquakeLocation: String
    val feltScale: String
    val noMmiReport: String
    val distance: String
    fun distanceFromYou(km: Double): String
    val calculatingDistance: String
    val viewFullDetails: String
    val monitoringLocation: String
    val locationUnavailable: String
    val enableGpsPrompt: String
    val enableLocationPermission: String
    val otherEarthquakes: String
    val noRecentEarthquakes: String
    val noRecentEarthquakesDesc: String
    val activeAlertTitle: String
    val testSirenButton: String
    val dataSourceBmkg: String
    val shareTitle: String
    fun shareFormat(mag: Double, region: String, time: String, depth: Double, lat: Double, lon: Double): String

    // Settings Screen
    val settingsTitle: String
    val themeSection: String
    val themeSubtitle: String
    val themeSystem: String
    val themeLight: String
    val themeDark: String
    val languageSection: String
    val languageSubtitle: String
    val languageSystem: String
    val languageIndonesian: String
    val languageEnglish: String
    val alertsSection: String
    val alertsTitle: String
    val alertsSubtitle: String
    val criteriaSection: String
    val strongQuakeTitle: String
    val strongQuakeSubtitle: String
    val feltQuakeTitle: String
    val feltQuakeSubtitle: String
    val distanceModeTitle: String
    val distanceModeAuto: String
    val distanceModeCustom: String
    val notificationSection: String
    val soundTitle: String
    val soundSubtitle: String
    val vibrationTitle: String
    val vibrationSubtitle: String
    val fullScreenTitle: String
    val fullScreenSubtitle: String
    val testAlarmButton: String
    val aboutSection: String
    val aboutDataSource: String
    val aboutAppVersion: String
    val aboutArchitecture: String

    // History Screen
    val historyTitle: String
    val filterAll: String
    val filterMagnitude5: String
    val filterFelt: String
    val filterNearby: String
    val noHistoryData: String

    // Detail Screen
    val back: String
    val detailsTitle: String
    val paramsTable: String
    val tsunamiStatus: String
    val tsunamiThreat: String
    val tsunamiPotentialYes: String
    val tsunamiPotentialNo: String
    val feltReports: String
    val safetyGuidelines: String
    val guidelinesContent: String
    val distanceFromYouLabel: String
    val locationUnavailableDetail: String
    val unknownValue: String
    val officialBmkgData: String
    val shareVia: String
    val failedToLoadDetails: String

    // Active Alert Screen
    val alertWarning: String
    val alertDetected: String
    fun alertAwayFormat(km: Double): String
    fun alertRegionDepthFormat(region: String, depth: Double): String
    val alertSafetyInstruction: String
    val alertViewDetails: String
    val alertDismiss: String

    // Onboarding Screen
    val onboardingWelcomeTitle: String
    val onboardingWelcomeDesc: String
    val onboardingGetStarted: String
    val onboardingLocationTitle: String
    val onboardingLocationDesc: String
    val onboardingAllowLocation: String
    val onboardingSkip: String
    val onboardingNotificationTitle: String
    val onboardingNotificationDesc: String
    val onboardingEnableNotifications: String
    val onboardingReadyTitle: String
    val onboardingReadyDesc: String
    val onboardingContinue: String
}

object IndonesianStrings : Strings {
    override val navHome = "Beranda"
    override val navHistory = "Riwayat"
    override val navSettings = "Pengaturan"

    override val appTitle = "Earthquake Alert"
    override val latestEarthquakeSection = "Gempabumi Terkini"
    override val shakeMap = "Peta Guncangan"
    override val share = "Bagikan"
    override val magnitude = "Magnitudo"
    override val depth = "Kedalaman"
    override val coordinates = "Koordinat"
    override val feltEarthquake = "Gempabumi Dirasakan"
    override val tsunamiWarning = "Peringatan Dini Tsunami"
    override val significantEarthquake = "Gempabumi M ≥ 5.0"
    override val recentEarthquake = "Gempabumi Terkini"
    override val time = "Waktu :"
    override val earthquakeLocation = "Lokasi Gempa"
    override val feltScale = "Wilayah Dirasakan (Skala MMI)"
    override val noMmiReport = "Belum ada laporan dirasakan"
    override val distance = "Jarak"
    override fun distanceFromYou(km: Double) = "%.0f KM dari lokasi Anda".format(km)
    override val calculatingDistance = "Menghitung jarak dari lokasi Anda..."
    override val viewFullDetails = "Lihat rincian lengkap →"
    override val monitoringLocation = "Lokasi Pemantauan Anda"
    override val locationUnavailable = "Akses lokasi belum aktif"
    override val enableGpsPrompt = "Aktifkan GPS agar jarak ke episentrum gempa dapat dihitung akurat."
    override val enableLocationPermission = "Aktifkan Izin Lokasi"
    override val otherEarthquakes = "Aktivitas Gempa Lainnya"
    override val noRecentEarthquakes = "Tidak Ada Data Gempa"
    override val noRecentEarthquakesDesc = "Belum ada catatan gempa bumi terbaru."
    override val activeAlertTitle = "ALARM GEMPA AKTIF"
    override val testSirenButton = "Tes Bunyi Sirine Darurat"
    override val dataSourceBmkg = "Sumber data: BMKG (Badan Meteorologi, Klimatologi, dan Geofisika)"
    override val shareTitle = "Bagikan Informasi Gempa"
    override fun shareFormat(mag: Double, region: String, time: String, depth: Double, lat: Double, lon: Double): String {
        return "Gempabumi Mag: %.1f, %s\nWaktu: %s\nKedalaman: %.0f Km\nKoordinat: %.2f, %.2f\nSumber: BMKG".format(
            mag, region, time, depth, lat, lon
        )
    }

    override val settingsTitle = "Pengaturan"
    override val themeSection = "Tampilan (Theme)"
    override val themeSubtitle = "Pilih tema warna aplikasi"
    override val themeSystem = "Sistem"
    override val themeLight = "Light"
    override val themeDark = "Dark"
    override val languageSection = "Bahasa (Language)"
    override val languageSubtitle = "Pilih bahasa tampilan aplikasi"
    override val languageSystem = "Sistem"
    override val languageIndonesian = "Indonesia"
    override val languageEnglish = "English"
    override val alertsSection = "Peringatan Gempa"
    override val alertsTitle = "Peringatan gempa bumi"
    override val alertsSubtitle = "Aktifkan alarm untuk gempa yang relevan dengan lokasi Anda"
    override val criteriaSection = "Kriteria Peringatan"
    override val strongQuakeTitle = "Gempa signifikan (M ≥ 5.0)"
    override val strongQuakeSubtitle = "Selalu terima peringatan untuk peristiwa gempa berkekuatan besar"
    override val feltQuakeTitle = "Gempa dirasakan di wilayah"
    override val feltQuakeSubtitle = "Peringatkan gempa dengan laporan intensitas manusia (MMI)"
    override val distanceModeTitle = "Mode Jarak"
    override val distanceModeAuto = "Otomatis (Radius Pintar)"
    override val distanceModeCustom = "Kustom (150 km)"
    override val notificationSection = "Notifikasi & Sirine"
    override val soundTitle = "Bunyi sirine alarm"
    override val soundSubtitle = "Putar audio sirine saat peringatan darurat aktif"
    override val vibrationTitle = "Getaran"
    override val vibrationSubtitle = "Getarkan perangkat saat peringatan darurat"
    override val fullScreenTitle = "Layar penuh saat darurat"
    override val fullScreenSubtitle = "Nyalakan layar dan tampilkan panduan darurat walau HP terkunci"
    override val testAlarmButton = "Tes Sirine & Layar Penuh"
    override val aboutSection = "Tentang Aplikasi"
    override val aboutDataSource = "Sumber Data"
    override val aboutAppVersion = "Versi Aplikasi"
    override val aboutArchitecture = "Arsitektur"

    override val historyTitle = "Riwayat Gempa"
    override val filterAll = "Semua"
    override val filterMagnitude5 = "M ≥ 5.0"
    override val filterFelt = "Dirasakan"
    override val filterNearby = "Terdekat"
    override val noHistoryData = "Belum ada riwayat gempa."

    override val back = "← Kembali"
    override val detailsTitle = "Rincian Gempa"
    override val paramsTable = "Parameter Gempa"
    override val tsunamiStatus = "Status Tsunami"
    override val tsunamiThreat = "Potensi Tsunami"
    override val tsunamiPotentialYes = "BERPOTENSI TSUNAMI"
    override val tsunamiPotentialNo = "Tidak berpotensi tsunami"
    override val feltReports = "Laporan Dirasakan (MMI)"
    override val safetyGuidelines = "Panduan Keselamatan Gempa"
    override val guidelinesContent = "1. Tetap tenang dan jangan panik.\n2. Berlindung di bawah meja yang kokoh jika berada di dalam ruangan.\n3. Jauhi jendela kaca, cermin, dan benda gantung.\n4. Segera menuju lapangan terbuka jika sudah aman."
    override val distanceFromYouLabel = "Jarak dari lokasi Anda"
    override val locationUnavailableDetail = "Akses lokasi belum aktif"
    override val unknownValue = "Tidak diketahui"
    override val officialBmkgData = "Sumber data resmi BMKG Indonesia."
    override val shareVia = "Bagikan via"
    override val failedToLoadDetails = "Gagal memuat rincian gempa"

    // Active Alert Screen
    override val alertWarning = "PERINGATAN"
    override val alertDetected = "GEMPA BUMI TERDETEKSI"
    override fun alertAwayFormat(km: Double) = "%.0f km dari lokasi Anda".format(km)
    override fun alertRegionDepthFormat(region: String, depth: Double) = "%s | Kedalaman %.0f km".format(region, depth)
    override val alertSafetyInstruction = "Lindungi kepala Anda.\nJauhi kaca dan benda yang berpotensi jatuh."
    override val alertViewDetails = "Lihat Rincian"
    override val alertDismiss = "Tutup"

    // Onboarding Screen
    override val onboardingWelcomeTitle = "Earthquake Alert"
    override val onboardingWelcomeDesc = "Peringatan gempa lokal secara instan untuk wilayah Indonesia. Data resmi langsung dari BMKG."
    override val onboardingGetStarted = "Mulai"
    override val onboardingLocationTitle = "Akses Lokasi"
    override val onboardingLocationDesc = "Lokasi Anda diproses secara lokal di perangkat untuk menghitung jarak ke pusat gempa. Lokasi tidak pernah diunggah ke server luar."
    override val onboardingAllowLocation = "Izinkan Lokasi"
    override val onboardingSkip = "Lewati"
    override val onboardingNotificationTitle = "Notifikasi Darurat"
    override val onboardingNotificationDesc = "Izinkan notifikasi prioritas tinggi dan sirine agar Anda segera mendapat peringatan saat gempa terjadi di sekitar Anda."
    override val onboardingEnableNotifications = "Aktifkan Notifikasi"
    override val onboardingReadyTitle = "Siap Memantau"
    override val onboardingReadyDesc = "Pemantauan aktif. Aplikasi akan memeriksa data BMKG dan mengevaluasi getaran gempa terhadap lokasi Anda."
    override val onboardingContinue = "Lanjut ke Beranda"
}

object EnglishStrings : Strings {
    override val navHome = "Home"
    override val navHistory = "History"
    override val navSettings = "Settings"

    override val appTitle = "Earthquake Alert"
    override val latestEarthquakeSection = "Latest Earthquake"
    override val shakeMap = "Shake Map"
    override val share = "Share"
    override val magnitude = "Magnitude"
    override val depth = "Depth"
    override val coordinates = "Coordinates"
    override val feltEarthquake = "Felt Earthquake"
    override val tsunamiWarning = "Tsunami Warning"
    override val significantEarthquake = "Significant M ≥ 5.0"
    override val recentEarthquake = "Latest Earthquake"
    override val time = "Time :"
    override val earthquakeLocation = "Location"
    override val feltScale = "Felt Intensity (MMI Scale)"
    override val noMmiReport = "No felt reports recorded"
    override val distance = "Distance"
    override fun distanceFromYou(km: Double) = "%.0f km from your location".format(km)
    override val calculatingDistance = "Calculating distance from your location..."
    override val viewFullDetails = "View full details →"
    override val monitoringLocation = "Your Monitoring Location"
    override val locationUnavailable = "Location access unavailable"
    override val enableGpsPrompt = "Enable GPS to calculate accurate distance to the epicenter."
    override val enableLocationPermission = "Enable Location Access"
    override val otherEarthquakes = "Recent Regional Activity"
    override val noRecentEarthquakes = "No Recent Earthquakes"
    override val noRecentEarthquakesDesc = "No recent earthquake events recorded."
    override val activeAlertTitle = "ACTIVE EARTHQUAKE ALERT"
    override val testSirenButton = "Test Emergency Siren"
    override val dataSourceBmkg = "Source: BMKG (Meteorological, Climatological, and Geophysical Agency)"
    override val shareTitle = "Share Earthquake Info"
    override fun shareFormat(mag: Double, region: String, time: String, depth: Double, lat: Double, lon: Double): String {
        return "Earthquake Mag: %.1f, %s\nTime: %s\nDepth: %.0f Km\nCoordinates: %.2f, %.2f\nSource: BMKG".format(
            mag, region, time, depth, lat, lon
        )
    }

    override val settingsTitle = "Settings"
    override val themeSection = "Appearance (Theme)"
    override val themeSubtitle = "Select application theme"
    override val themeSystem = "System"
    override val themeLight = "Light"
    override val themeDark = "Dark"
    override val languageSection = "Language"
    override val languageSubtitle = "Select display language"
    override val languageSystem = "System"
    override val languageIndonesian = "Indonesian"
    override val languageEnglish = "English"
    override val alertsSection = "Alerts"
    override val alertsTitle = "Earthquake alerts"
    override val alertsSubtitle = "Enable alerts for earthquakes relevant to your location"
    override val criteriaSection = "Alert Criteria"
    override val strongQuakeTitle = "Strong earthquakes (M ≥ 5.0)"
    override val strongQuakeSubtitle = "Always receive alerts for significant regional events"
    override val feltQuakeTitle = "Felt earthquakes in region"
    override val feltQuakeSubtitle = "Alert on earthquakes with reported human-felt intensity (MMI)"
    override val distanceModeTitle = "Distance Mode"
    override val distanceModeAuto = "Auto (Smart Radius)"
    override val distanceModeCustom = "Custom (150 km)"
    override val notificationSection = "Notification & Siren"
    override val soundTitle = "Siren sound alert"
    override val soundSubtitle = "Play siren audio when emergency alert triggers"
    override val vibrationTitle = "Vibration"
    override val vibrationSubtitle = "Vibrate device during emergency alert"
    override val fullScreenTitle = "Full-screen alert"
    override val fullScreenSubtitle = "Turn on screen and display instructions even when locked"
    override val testAlarmButton = "Test Siren & Full-Screen Alarm"
    override val aboutSection = "About"
    override val aboutDataSource = "Data Source"
    override val aboutAppVersion = "Application Version"
    override val aboutArchitecture = "Architecture"

    override val historyTitle = "Earthquake History"
    override val filterAll = "All"
    override val filterMagnitude5 = "M ≥ 5.0"
    override val filterFelt = "Felt"
    override val filterNearby = "Nearby"
    override val noHistoryData = "No earthquake history found."

    override val back = "← Back"
    override val detailsTitle = "Earthquake Details"
    override val paramsTable = "Earthquake Parameters"
    override val tsunamiStatus = "Tsunami Status"
    override val tsunamiThreat = "Tsunami Threat"
    override val tsunamiPotentialYes = "YES - Tsunami Advisory"
    override val tsunamiPotentialNo = "No tsunami potential"
    override val feltReports = "Felt Reports (MMI)"
    override val safetyGuidelines = "Earthquake Safety Guidelines"
    override val guidelinesContent = "1. Stay calm and do not panic.\n2. Drop, cover, and hold on under a sturdy table if indoors.\n3. Stay away from glass windows and hanging objects.\n4. Evacuate to an open area once shaking stops."
    override val distanceFromYouLabel = "Distance from you"
    override val locationUnavailableDetail = "Location not available"
    override val unknownValue = "Unknown"
    override val officialBmkgData = "Official data provided by BMKG Indonesia."
    override val shareVia = "Share via"
    override val failedToLoadDetails = "Failed to load details"

    // Active Alert Screen
    override val alertWarning = "WARNING"
    override val alertDetected = "EARTHQUAKE DETECTED"
    override fun alertAwayFormat(km: Double) = "%.0f km away".format(km)
    override fun alertRegionDepthFormat(region: String, depth: Double) = "%s | Depth %.0f km".format(region, depth)
    override val alertSafetyInstruction = "Protect your head.\nMove away from glass and objects that may fall."
    override val alertViewDetails = "View Details"
    override val alertDismiss = "Dismiss"

    // Onboarding Screen
    override val onboardingWelcomeTitle = "Earthquake Alert"
    override val onboardingWelcomeDesc = "Instant, local alerts for significant earthquakes detected across Indonesia. Data directly from BMKG."
    override val onboardingGetStarted = "Get Started"
    override val onboardingLocationTitle = "Location Access"
    override val onboardingLocationDesc = "Your location is used locally on your device to calculate distance from earthquake epicenters. Your location is never uploaded to any external server."
    override val onboardingAllowLocation = "Allow Location"
    override val onboardingSkip = "Skip for Now"
    override val onboardingNotificationTitle = "Emergency Notifications"
    override val onboardingNotificationDesc = "Allow high-priority notifications and audio siren alerts so you are promptly alerted when an earthquake is nearby."
    override val onboardingEnableNotifications = "Enable Notifications"
    override val onboardingReadyTitle = "Ready to Monitor"
    override val onboardingReadyDesc = "Protection is active. The application periodically checks BMKG feeds and evaluates tremors against your location."
    override val onboardingContinue = "Continue to Dashboard"
}

val LocalAppStrings = staticCompositionLocalOf<Strings> { IndonesianStrings }

object AppStrings {
    val current: Strings
        @Composable
        @ReadOnlyComposable
        get() = LocalAppStrings.current

    fun resolve(appLanguage: AppLanguage): Strings {
        return when (appLanguage) {
            AppLanguage.INDONESIAN -> IndonesianStrings
            AppLanguage.ENGLISH -> EnglishStrings
            AppLanguage.SYSTEM -> {
                val lang = Locale.getDefault().language.lowercase()
                if (lang == "in" || lang == "id") IndonesianStrings else EnglishStrings
            }
        }
    }
}
