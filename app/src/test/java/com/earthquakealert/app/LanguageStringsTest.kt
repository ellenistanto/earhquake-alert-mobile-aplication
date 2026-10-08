package com.earthquakealert.app

import com.earthquakealert.app.domain.model.AppLanguage
import com.earthquakealert.app.ui.i18n.AppStrings
import com.earthquakealert.app.ui.i18n.EnglishStrings
import com.earthquakealert.app.ui.i18n.IndonesianStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LanguageStringsTest {

    @Test
    fun testResolveIndonesianLanguage() {
        val strings = AppStrings.resolve(AppLanguage.INDONESIAN)
        assertEquals(IndonesianStrings, strings)
        assertEquals("Beranda", strings.navHome)
        assertEquals("Riwayat", strings.navHistory)
        assertEquals("Pengaturan", strings.navSettings)
        assertEquals("Gempabumi Terkini", strings.latestEarthquakeSection)
        assertEquals("Indonesia", strings.languageIndonesian)
        assertEquals("English", strings.languageEnglish)
        assertEquals("← Kembali", strings.back)
        assertEquals("PERINGATAN", strings.alertWarning)
        assertEquals("Mulai", strings.onboardingGetStarted)
    }

    @Test
    fun testResolveEnglishLanguage() {
        val strings = AppStrings.resolve(AppLanguage.ENGLISH)
        assertEquals(EnglishStrings, strings)
        assertEquals("Home", strings.navHome)
        assertEquals("History", strings.navHistory)
        assertEquals("Settings", strings.navSettings)
        assertEquals("Latest Earthquake", strings.latestEarthquakeSection)
        assertEquals("Indonesian", strings.languageIndonesian)
        assertEquals("English", strings.languageEnglish)
        assertEquals("← Back", strings.back)
        assertEquals("WARNING", strings.alertWarning)
        assertEquals("Get Started", strings.onboardingGetStarted)
    }

    @Test
    fun testFormattedStrings() {
        val idStrings = IndonesianStrings
        val enStrings = EnglishStrings

        val idDistance = idStrings.distanceFromYou(25.0)
        assertTrue(idDistance.contains("25 KM"))

        val enDistance = enStrings.distanceFromYou(25.0)
        assertTrue(enDistance.contains("25 km"))

        val idAlert = idStrings.alertAwayFormat(50.0)
        assertTrue(idAlert.contains("50 km"))

        val enAlert = enStrings.alertAwayFormat(50.0)
        assertTrue(enAlert.contains("50 km"))

        val shareId = idStrings.shareFormat(6.1, "Yogyakarta", "21:00 WIB", 10.0, -7.8, 110.4)
        assertNotNull(shareId)
        assertTrue(shareId.contains("BMKG"))

        val shareEn = enStrings.shareFormat(6.1, "Yogyakarta", "21:00 WIB", 10.0, -7.8, 110.4)
        assertNotNull(shareEn)
        assertTrue(shareEn.contains("BMKG"))
    }
}
