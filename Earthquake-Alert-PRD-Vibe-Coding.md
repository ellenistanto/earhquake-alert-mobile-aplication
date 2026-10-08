# Earthquake Alert — Product Requirements Document (PRD)

> **Purpose:** PRD ini menjadi sumber kebenaran utama untuk vibe coding / AI coding agent.
> Implementasikan MVP terlebih dahulu, jangan menambahkan fitur di luar scope tanpa alasan yang jelas.

---

## 1. Project Overview

**Project Name:** Earthquake Alert  
**Platform:** Android Mobile  
**Language:** Kotlin  
**UI:** Jetpack Compose  
**Architecture:** MVVM + Repository Pattern  
**Build:** Gradle Kotlin DSL  
**Minimum Android:** Android 10+  
**Development Status:** Greenfield / from scratch

### Product statement

Earthquake Alert adalah aplikasi Android yang memberi peringatan kepada pengguna ketika ada gempa yang secara geografis berpotensi relevan dengan lokasi pengguna.

Aplikasi **tidak memprediksi gempa**. Aplikasi hanya memproses informasi gempa yang sudah diterbitkan oleh sumber data dan kemudian menentukan apakah event tersebut cukup relevan untuk memicu alert.

### Core value

**Fast. Relevant. Loud. Simple.**

---

# 2. Problem

Pengguna sering terlambat menyadari gempa karena:

- getaran kecil sulit dikenali,
- pengguna sedang tidur,
- berada di tempat ramai,
- menggunakan earphone,
- smartphone dalam mode silent / Do Not Disturb,
- informasi dari media sosial atau berita tidak langsung berfokus pada lokasi pengguna.

Earthquake Alert harus mengurangi waktu yang dibutuhkan pengguna untuk menyadari adanya gempa relevan dan mengambil tindakan keselamatan.

---

# 3. Goals

## Primary goals

1. Menerima informasi gempa dari sumber data eksternal.
2. Menyimpan dan memproses event gempa.
3. Menggunakan lokasi pengguna untuk menentukan relevansi gempa.
4. Mencegah duplicate alert untuk event yang sama.
5. Menampilkan emergency notification dengan prioritas tinggi.
6. Menampilkan informasi gempa secara cepat dan mudah dibaca.
7. Berfungsi saat app berada di background sesuai kemampuan Android.

## Secondary goals

1. Menyediakan riwayat gempa.
2. Menyediakan pengaturan alert.
3. Menyediakan status permission dan konfigurasi emergency notification.
4. Menunjukkan sumber data dengan jelas.

---

# 4. Non-Goals (MVP)

Jangan implementasikan fitur berikut pada MVP:

- akun/login,
- social features,
- chat,
- family sharing,
- live map yang kompleks,
- prediction AI,
- machine learning untuk prediksi gempa,
- sensor-based earthquake prediction,
- wearable integration,
- multi-country support,
- iOS,
- backend kompleks,
- payment/subscription,
- ads.

Fitur di atas boleh menjadi roadmap setelah MVP stabil.

---

# 5. Target Users

Target utama: masyarakat umum pengguna Android.

Use cases utama:

### UC-01 — User sedang tidur

Gempa relevan terjadi saat layar terkunci. User harus menerima alert yang menarik perhatian.

### UC-02 — User sedang menggunakan app lain

Alert harus dapat muncul tanpa user harus membuka Earthquake Alert.

### UC-03 — User berada di area dekat episenter

Gempa yang relevan terhadap lokasi user diprioritaskan.

### UC-04 — User berada jauh dari gempa kecil

App tidak boleh mengirim emergency alert untuk semua event gempa.

---

# 6. Product Principles

Gunakan prinsip berikut selama development:

1. **Emergency first** — informasi paling penting harus terlihat terlebih dahulu.
2. **Simple over decorative** — jangan menambahkan UI yang hanya mempercantik.
3. **Fast over fancy** — kurangi animasi dan proses yang tidak perlu.
4. **Reliable over clever** — gunakan logic yang mudah dites.
5. **Minimal permission** — minta hanya permission yang benar-benar dibutuhkan.
6. **Explain uncertainty** — jangan membuat klaim prediksi yang tidak dapat dibuktikan.
7. **No AI-slop UI** — hindari gradient berlebihan, glassmorphism, blob, card berlapis, icon random, dan dashboard generik.

---

# 7. MVP Feature Scope

## P0 — Mandatory

- First launch onboarding
- Location permission
- Notification permission
- Earthquake data fetching
- Earthquake JSON parsing
- Local database
- User location handling
- Distance calculation
- Alert eligibility evaluator
- Emergency notification
- Alarm sound
- Vibration
- Duplicate event prevention
- Home screen
- Earthquake detail screen
- History screen
- Settings screen
- Error / offline states
- BMKG source attribution

## P1 — After MVP works

- Firebase Cloud Messaging
- Lightweight backend
- More reliable server-side alert fan-out
- Better geo filtering
- Improved emergency notification behavior
- More advanced event severity calculation

---

# 8. Recommended Tech Stack

## Android

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX
- ViewModel
- StateFlow
- Coroutines
- Room
- DataStore Preferences
- WorkManager
- NotificationManager
- Location Services
- Retrofit
- OkHttp
- Kotlin Serialization atau Gson
- JUnit
- AndroidX Test

## Package structure

```text
com.earthquakealert.app

├── data
│   ├── local
│   │   ├── dao
│   │   ├── entity
│   │   └── database
│   ├── remote
│   │   ├── api
│   │   ├── dto
│   │   └── service
│   └── repository
│
├── domain
│   ├── model
│   ├── repository
│   └── usecase
│
├── location
│
├── notification
│
├── evaluator
│
├── ui
│   ├── home
│   ├── alert
│   ├── detail
│   ├── history
│   ├── settings
│   └── onboarding
│
├── navigation
│
├── worker
│
└── util
```

Keep domain logic independent from Android UI where practical.

---

# 9. External Data Source

## Primary source

Use the BMKG earthquake data endpoint(s) appropriate for the implemented environment.

The implementation must isolate the external API behind:

```kotlin
EarthquakeRemoteDataSource
```

Do not put API parsing logic directly in composables or ViewModels.

## Earthquake fields

Support at minimum:

```text
event id
timestamp
latitude
longitude
magnitude
depth
region/location
tsunami potential
mmi / felt information if available
```

Because the exact API response may change, isolate DTOs from domain models.

---

# 10. Core Domain Model

Create a domain model similar to:

```kotlin
data class Earthquake(
    val id: String,
    val timestamp: Instant,
    val latitude: Double,
    val longitude: Double,
    val magnitude: Double,
    val depthKm: Double?,
    val region: String,
    val tsunamiPotential: Boolean?,
    val mmi: Double?,
    val source: String
)
```

User location:

```kotlin
data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float?,
    val updatedAt: Instant
)
```

Computed event relevance:

```kotlin
data class EarthquakeAssessment(
    val earthquakeId: String,
    val distanceKm: Double,
    val shouldAlert: Boolean,
    val severity: AlertSeverity,
    val reason: String
)
```

---

# 11. Database

Use Room.

## EarthquakeEntity

```text
id
timestamp
latitude
longitude
magnitude
depthKm
region
tsunamiPotential
mmi
source
distanceKm
alertTriggered
createdAt
```

## ProcessedEventEntity

```text
eventId
processedAt
alertTriggered
```

Purpose:

- history,
- caching,
- duplicate prevention,
- audit/debugging.

---

# 12. User Settings

Persist using DataStore.

Suggested fields:

```text
alertEnabled: Boolean
strongEarthquakeEnabled: Boolean
feltEarthquakeEnabled: Boolean
selectedDistanceMode: Auto / Custom
customDistanceKm: Int?
soundEnabled: Boolean
vibrationEnabled: Boolean
fullScreenAlertEnabled: Boolean
```

Do not expose overly complicated settings in MVP.

---

# 13. Location Strategy

Do NOT continuously track GPS every few seconds.

Preferred strategy:

1. Request location permission on onboarding.
2. Obtain a suitable last-known/current location.
3. Refresh location periodically or when needed.
4. Store the most recent valid location in memory/DataStore.
5. Use the latest available location when evaluating an earthquake.

The app should tolerate:

- location unavailable,
- GPS disabled,
- approximate location,
- stale location.

If location is unavailable, do not crash.

---

# 14. Distance Calculation

Use the Haversine formula.

Pseudo-code:

```text
distanceKm =
    haversine(
        userLatitude,
        userLongitude,
        earthquakeLatitude,
        earthquakeLongitude
    )
```

Create a reusable pure function:

```kotlin
fun calculateDistanceKm(
    userLat: Double,
    userLon: Double,
    quakeLat: Double,
    quakeLon: Double
): Double
```

Unit tests must cover:

- same coordinates,
- known coordinate pair,
- very short distance,
- large distance.

---

# 15. Alert Evaluation Logic

## Important

Do NOT use:

```text
if magnitude >= X then alert
```

as the only criterion.

Use multiple signals:

- magnitude,
- distance,
- depth,
- felt/MMI information when available,
- user settings.

The MVP evaluator may use a deterministic heuristic.

Example concept:

```text
VERY_NEAR:
distance <= 25 km
and magnitude >= configured threshold
=> HIGH

NEAR:
distance <= 75 km
and magnitude is significant
=> MEDIUM/HIGH

FAR:
distance > 75 km
=> usually no emergency alert unless other strong evidence exists
```

These thresholds are initial engineering heuristics, not scientific prediction thresholds.

Keep all thresholds centralized:

```kotlin
object AlertThresholds
```

Do not scatter magic numbers across the project.

---

# 16. Alert Severity

Define:

```kotlin
enum class AlertSeverity {
    NONE,
    INFO,
    WARNING,
    HIGH
}
```

Example mapping:

```text
NONE
  no notification

INFO
  history / low priority notification

WARNING
  normal high-priority alert

HIGH
  emergency notification + sound + vibration + full-screen if permitted
```

The exact mapping must remain configurable inside the evaluator.

---

# 17. Duplicate Alert Protection

Every earthquake event has a stable ID.

Flow:

```text
Receive event
    ↓
Check event ID in local DB
    ↓
Already processed?
 ┌───────┴───────┐
 YES             NO
  ↓               ↓
Ignore       Evaluate
                  ↓
             Trigger alert?
            ┌─────┴─────┐
           YES          NO
            ↓            ↓
      Save processed   Save processed
```

Never send the same emergency alert repeatedly for the same event unless the product specification explicitly adds alert escalation later.

---

# 18. Background Processing

For MVP prototype:

```text
WorkManager
    ↓
Fetch latest earthquake data
    ↓
Parse response
    ↓
Save/update local DB
    ↓
Evaluate latest events
    ↓
Trigger notification
```

Important:

WorkManager is not guaranteed to deliver second-level real-time polling. It is acceptable for an MVP/prototype but should not be presented as guaranteed real-time early warning.

For production, move toward:

```text
BMKG / upstream source
        ↓
Backend event processor
        ↓
Firebase Cloud Messaging
        ↓
Android app
```

The app should already separate data ingestion from UI so this migration is possible later.

---

# 19. Notification System

Use Android Notification Channels.

Create at least:

```text
earthquake_emergency
earthquake_general
```

Emergency channel requirements:

- high importance,
- vibration enabled,
- dedicated alarm sound,
- appropriate category,
- visible notification,
- notification content that is immediately understandable.

Potential emergency flow:

```text
Earthquake qualifies
      ↓
NotificationManager
      ↓
Emergency channel
      ↓
High-priority notification
      ↓
Sound
      ↓
Vibration
      ↓
Full-screen alert where supported/allowed
```

## DND / Silent requirement

Do not claim that the app can always override DND or silent mode.

Instead:

- use supported Android notification mechanisms,
- provide a setup page,
- clearly explain what permissions/settings are required,
- gracefully degrade if the OS does not allow the requested behavior.

The app must never crash because DND bypass is unavailable.

---

# 20. Alarm Sound

Use a bundled emergency sound asset.

Requirements:

- short but attention-grabbing,
- not excessively long,
- loops only if technically justified,
- stop when user acknowledges the alert.

Do not depend on arbitrary music files from the user's device.

Provide:

```text
Preview Alarm
```

inside Settings.

---

# 21. Vibration

Emergency vibration pattern should be distinctive.

Example:

```text
[500ms pause 300ms 500ms]
```

Keep the pattern centralized.

Make vibration optional in settings.

---

# 22. Full-Screen Alert

When supported and permitted:

```text
Emergency alert activity
```

Use only for truly urgent events.

The screen should:

- occupy most/all of available display,
- show the severity,
- show magnitude,
- show approximate distance,
- show event region,
- provide one clear action such as "View Details" or "Dismiss".

Do not add unnecessary navigation.

---

# 23. Screen Specification

# 23.1 Onboarding

Purpose: explain the product and request permissions.

Screen sequence:

```text
Welcome
  ↓
Why notifications are needed
  ↓
Why location is needed
  ↓
Emergency alert setup
  ↓
Finish
```

Use concise text.

Example:

```text
Earthquake Alert

Get an alert when a nearby
earthquake may affect you.

[ Get Started ]
```

---

# 23.2 Home

Main status screen.

Required content:

```text
App name
Protection status
Current location status
Latest relevant earthquake
Quick access to History
Quick access to Settings
```

Suggested structure:

```text
Earthquake Alert

● Protection Active

Yogyakarta
Location updated 2 min ago

No active alert

Latest Earthquake
M 4.2
126 km away
Central Java
16:32 WIB
```

Keep the screen visually quiet when there is no emergency.

---

# 23.3 Active Alert Screen

When an emergency event is active:

```text
EARTHQUAKE DETECTED

M 5.4

32 km away
Depth 10 km

South of Java

[ View Details ]

Take care and protect yourself.
```

Priority is:

1. event status,
2. magnitude,
3. distance,
4. location,
5. action.

---

# 23.4 Earthquake Detail

Show:

```text
Magnitude
Depth
Distance
Location
Time
Coordinates
Tsunami status
MMI / felt information when available
Data source
```

No unnecessary charts in MVP.

---

# 23.5 History

Show chronological events.

Example item:

```text
M 4.8
56 km away
Yogyakarta
17:42 WIB
```

Use lazy lists.

Support:

```text
Today
This Week
This Month
```

Optional filtering may be added after the basic list works.

---

# 23.6 Settings

Sections:

```text
Alerts
Sound
Vibration
Emergency display

Location
Permission status

System setup
Notification permission
DND / critical alert guidance

About
Data source
App version
```

---

# 24. UI / UX Design System

## Visual direction

Minimalist emergency utility.

### Do

- strong typography,
- high contrast,
- clean spacing,
- restrained borders,
- simple icons,
- clear hierarchy,
- consistent corner radius,
- subtle transitions.

### Avoid

- glassmorphism,
- excessive cards,
- giant gradients,
- abstract blobs,
- decorative 3D illustrations,
- excessive shadows,
- fake data visualizations,
- generic AI-generated dashboard patterns.

## Layout rule

The UI should feel like a real utility app, not a startup landing page.

---

# 25. Color Guidance

Base UI:

```text
Background: neutral/off-white or dark neutral
Primary text: near-black / near-white
Secondary text: muted neutral
Border: subtle neutral
```

Semantic colors:

```text
Success: green
Warning: amber
Emergency: red
```

Use emergency red only when an actual alert is active. Do not make the whole normal application red.

---

# 26. Accessibility

Required:

- readable font sizes,
- sufficient contrast,
- content descriptions for icons,
- touch targets >= recommended Android minimum,
- support system font scaling,
- do not rely on color alone,
- emergency information remains understandable without animation.

---

# 27. Error States

Handle explicitly:

## No Internet

```text
Unable to update earthquake data.
Showing the latest saved information.
```

## Location unavailable

```text
Location unavailable.
Alert relevance may be limited.
```

## Notification permission denied

```text
Emergency alerts are disabled.
Enable notifications in system settings.
```

## DND bypass unavailable

```text
Critical alert access is not available.
Check Android notification settings.
```

Never leave users on a blank screen.

---

# 28. Loading States

Use lightweight loading indicators.

Avoid skeleton overload.

Examples:

```text
Loading latest earthquake...
Updating location...
```

---

# 29. Navigation

Use Compose Navigation.

Routes:

```text
onboarding
home
alert
history
detail/{earthquakeId}
settings
```

Do not build a complicated nested navigation hierarchy for MVP.

---

# 30. State Management

ViewModels expose immutable UI state.

Example:

```kotlin
data class HomeUiState(
    val isLoading: Boolean = false,
    val protectionActive: Boolean = false,
    val location: UserLocationUi? = null,
    val latestEarthquake: EarthquakeUi? = null,
    val error: String? = null
)
```

Use:

```text
StateFlow
collectAsStateWithLifecycle()
```

Avoid mutable global singleton state unless strictly required.

---

# 31. Repository Rules

UI must not talk directly to:

- Retrofit,
- Room DAO,
- Location API,
- NotificationManager.

Use abstractions:

```text
UI
 ↓
ViewModel
 ↓
UseCase
 ↓
Repository
 ↓
Remote / Local / Android service
```

---

# 32. Suggested Interfaces

```kotlin
interface EarthquakeRepository {
    suspend fun getLatestEarthquakes(): List<Earthquake>
    fun observeHistory(): Flow<List<Earthquake>>
    suspend fun getEarthquakeById(id: String): Earthquake?
}
```

```kotlin
interface LocationRepository {
    suspend fun getLastKnownLocation(): UserLocation?
    suspend fun refreshLocation(): UserLocation?
}
```

```kotlin
interface AlertEvaluator {
    fun evaluate(
        earthquake: Earthquake,
        userLocation: UserLocation,
        settings: AlertSettings
    ): EarthquakeAssessment
}
```

```kotlin
interface NotificationService {
    fun sendEarthquakeAlert(
        earthquake: Earthquake,
        assessment: EarthquakeAssessment
    )
}
```

---

# 33. Main Use Cases

Implement at least:

```text
FetchLatestEarthquakesUseCase
EvaluateEarthquakeUseCase
ProcessNewEarthquakeUseCase
GetEarthquakeHistoryUseCase
GetEarthquakeDetailUseCase
GetUserLocationUseCase
UpdateAlertSettingsUseCase
RequestNotificationPermissionUseCase
```

---

# 34. Testing Requirements

## Unit tests

Must test:

### Distance calculator

- same point = 0 km
- known coordinates
- edge cases

### Alert evaluator

- close + significant magnitude -> alert
- far + low magnitude -> no emergency alert
- boundary conditions
- custom user settings

### Duplicate prevention

- same event ID -> processed once

### Data parser

- valid response
- missing optional values
- malformed response

## Instrumentation tests

Test:

- onboarding,
- permission states,
- navigation,
- history rendering,
- detail screen,
- settings screen.

---

# 35. Acceptance Criteria

## AC-01 — Onboarding

Given the app is opened for the first time,
when onboarding is completed,
then the app has requested the required permissions and reaches Home.

## AC-02 — Location

Given a valid location permission,
when the app requests the latest location,
then it stores a usable location or handles failure gracefully.

## AC-03 — Data fetch

Given the external earthquake endpoint is available,
when a fetch is executed,
then earthquake events are parsed and stored locally.

## AC-04 — Distance

Given user coordinates and earthquake coordinates,
when the evaluator runs,
then the distance is calculated in kilometers.

## AC-05 — Relevance

Given an earthquake event meets the evaluator rules,
when it is processed,
then an alert assessment with non-NONE severity is generated.

## AC-06 — Emergency notification

Given an event has HIGH severity,
when notification permissions and system capabilities allow it,
then the app triggers the emergency notification flow.

## AC-07 — Duplicate prevention

Given the same earthquake event is processed again,
when its ID already exists in the processed-event database,
then another emergency alert is not sent.

## AC-08 — Background

Given the app is not in the foreground,
when the background worker processes a qualifying event,
then the app attempts to show the configured alert using Android-supported mechanisms.

## AC-09 — History

Given an earthquake event has been stored,
when the user opens History,
then the event is displayed.

## AC-10 — Error handling

Given network or location failure,
when the operation fails,
then the app shows a meaningful state instead of crashing.

---

# 36. MVP Development Order

Implement in this order:

```text
1. Project setup
2. Compose theme
3. Navigation skeleton
4. Domain models
5. BMKG API client
6. Parser + repository
7. Room database
8. Location repository
9. Haversine calculator
10. Alert evaluator
11. Notification channel
12. Emergency notification
13. WorkManager worker
14. Home screen
15. History
16. Detail
17. Settings
18. Onboarding
19. Unit tests
20. Device testing
```

Do not polish UI before the core data → evaluate → alert pipeline works.

---

# 37. Development Milestones

## Milestone 1 — Skeleton

Expected result:

- app builds,
- Compose launches,
- navigation works,
- basic screens exist.

## Milestone 2 — Data

Expected result:

- API request works,
- parser works,
- local DB works.

## Milestone 3 — Location

Expected result:

- permission flow works,
- location is retrievable,
- distance calculation works.

## Milestone 4 — Alert Engine

Expected result:

- earthquake event can be evaluated,
- duplicate events are ignored.

## Milestone 5 — Notification

Expected result:

- emergency channel works,
- sound works,
- vibration works,
- full-screen behavior is tested where supported.

## Milestone 6 — Complete MVP

Expected result:

- onboarding,
- home,
- alert,
- detail,
- history,
- settings,
- error states,
- tests.

---

# 38. Demo / Test Mode

Build a developer-only test mechanism.

Example:

```text
Debug menu
    ↓
Simulate Earthquake
    ↓
Magnitude
Distance
Depth
Region
    ↓
Trigger Alert
```

Purpose:

- test alarm,
- test notification,
- test full-screen behavior,
- test duplicate prevention,
- test UI without waiting for a real earthquake.

This menu must not be visible in release builds.

---

# 39. Logging

Use structured logs only in debug builds.

Log examples:

```text
Earthquake fetched: <eventId>
Distance calculated: <distanceKm>
Assessment: <severity>
Alert triggered: <eventId>
Duplicate ignored: <eventId>
```

Never log sensitive location data unnecessarily in production builds.

---

# 40. Privacy

Principles:

- no account required for MVP,
- no unnecessary personal data,
- location used only for earthquake relevance,
- do not upload exact user location to a backend in MVP,
- if a backend is introduced later, minimize stored location precision and document the reason.

Add an in-app Privacy section before production release.

---

# 41. Source Attribution

The app must clearly identify the earthquake data source.

Example:

```text
Earthquake data provided by BMKG
```

Do not imply that BMKG endorses the application unless explicit authorization exists.

---

# 42. Important Technical Constraints

1. Android background execution is restricted.
2. Periodic WorkManager execution is not guaranteed to be real-time.
3. DND/silent override behavior is controlled by Android, permissions, user settings, and device manufacturer.
4. Full-screen emergency behavior must follow the platform's current rules.
5. Network outage can prevent newly published earthquake information from reaching the device.
6. Alert thresholds are heuristics unless validated against an appropriate scientific model/data source.
7. Never advertise the app as an earthquake prediction system.

---

# 43. Coding Standards for AI Coding Agent

When generating code:

### Must

- use Kotlin idioms,
- prefer immutable state,
- use suspend/Flow appropriately,
- separate UI/data/domain,
- write testable pure functions,
- centralize configuration constants,
- add error handling,
- keep functions focused,
- use meaningful names,
- avoid duplicated logic,
- update documentation when architecture changes.

### Must not

- create giant single-file implementations,
- put all logic inside MainActivity,
- put API calls inside composables,
- use hardcoded mock data in release code,
- use magic numbers throughout the codebase,
- silently swallow exceptions,
- add libraries without need,
- over-engineer the project,
- add decorative UI unrelated to the emergency use case.

---

# 44. Definition of Done

MVP is considered complete when:

- [ ] Android project builds successfully.
- [ ] App runs on a physical Android device.
- [ ] Onboarding works.
- [ ] Notification permission flow works.
- [ ] Location permission flow works.
- [ ] Latest earthquake data can be fetched.
- [ ] Data is stored locally.
- [ ] Distance calculation is tested.
- [ ] Alert evaluation is tested.
- [ ] Duplicate prevention works.
- [ ] Emergency notification works when permitted by Android.
- [ ] Sound and vibration work.
- [ ] Background processing works within platform limitations.
- [ ] Home screen works.
- [ ] History works.
- [ ] Detail screen works.
- [ ] Settings work.
- [ ] Error states work.
- [ ] Debug simulation works.
- [ ] No crashes in normal MVP flows.
- [ ] BMKG attribution is visible.
- [ ] No unsupported claim that the app predicts earthquakes.

---

# 45. AI Coding Agent Execution Rules

Use this PRD as the source of truth.

When starting implementation:

1. Inspect the existing repository.
2. If empty, initialize the Android project from scratch.
3. Create the architecture and package structure first.
4. Implement one milestone at a time.
5. After each milestone, run/build/test the project.
6. Fix compile errors before moving to the next milestone.
7. Never invent API fields without checking the actual response format.
8. Keep external API integration isolated behind repository/data-source abstractions.
9. Do not replace real data with mocks unless explicitly building debug/test mode.
10. Use the debug earthquake simulator for notification testing.
11. Prefer the smallest implementation that satisfies each requirement.
12. Do not add unrelated features.
13. When Android platform restrictions prevent a requirement from being guaranteed, implement the closest supported behavior and document the limitation in code comments/docs.

---

# 46. First Implementation Task

Start with:

```text
PHASE 1

1. Initialize Android Kotlin project.
2. Configure Jetpack Compose.
3. Configure Material 3.
4. Create package structure.
5. Create navigation.
6. Create placeholder screens:
   - Onboarding
   - Home
   - Active Alert
   - History
   - Detail
   - Settings
7. Create base theme.
8. Make the UI build successfully.
9. Add README describing how to run the project.

Do NOT implement API, location, database, or notification logic yet.
```

After Phase 1 is complete, proceed to the next milestone only after the project builds successfully.
