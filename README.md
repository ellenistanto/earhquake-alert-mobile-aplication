# Earthquake Alert (Android)

Android emergency alert application for earthquakes potentially relevant to the user's location.

Source of truth:
- Requirements & architecture: [Earthquake-Alert-PRD-Vibe-Coding.md](file:///d:/Coding/earhquake-alert/Earthquake-Alert-PRD-Vibe-Coding.md)
- UI/UX & Design system: [Earthquake-Alert-DESIGN.md](file:///d:/Coding/earhquake-alert/Earthquake-Alert-DESIGN.md)

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- MVVM Architecture
- Gradle Kotlin DSL (AGP 9.1.0, Kotlin 2.2.10)
- Target SDK: 35 | Min SDK: 26

## Project Structure

```text
com.earthquakealert.app
├── domain
│   ├── model       // Earthquake, UserLocation, Assessment, Settings
│   └── repository  // EarthquakeRepository, LocationRepository, SettingsRepository
├── evaluator       // AlertEvaluator, AlertThresholds
├── notification    // NotificationService
├── navigation      // Screen routes, NavHost, BottomNavigation
└── ui
    ├── theme       // Color, Spacing, Shape, Type, Theme tokens
    ├── components  // ProtectionStatus, Buttons, AppDivider
    ├── onboarding  // First-launch permission explanations
    ├── home        // Status, location, latest earthquake
    ├── alert       // Emergency Active Alert screen
    ├── detail      // Earthquake parameters
    ├── history     // Chronological earthquake list
    └── settings    // Alert settings and system guidance
```

## How to Build and Run

### Prerequisites
- JDK 17 or higher (JDK 23 supported)
- Android SDK (API 35, Build-tools 35+)

### Build Debug APK
```bash
./gradlew assembleDebug
```

### Run Unit Tests
```bash
./gradlew testDebugUnitTest
```
