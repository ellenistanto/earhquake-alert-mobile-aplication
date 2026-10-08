# Earthquake Alert — Design System & UI Specification

> **Purpose:** File ini adalah source of truth untuk UI/UX Earthquake Alert.
> Gunakan bersama `PRD.md`. Jangan membuat keputusan visual yang bertentangan dengan dokumen ini tanpa alasan UX yang kuat.

---

# 1. Design Direction

## Design Concept

**Minimal Emergency Utility**

Earthquake Alert harus terasa seperti aplikasi utilitas penting yang bisa dipercaya, bukan aplikasi startup yang penuh dekorasi.

Karakter visual:

- minimal
- calm saat kondisi normal
- high contrast saat emergency
- typography-first
- functional
- compact
- modern Android
- sedikit dekorasi
- fokus pada informasi

### Core design keywords

```text
Calm
Clear
Fast
Trustworthy
Functional
Human
```

---

# 2. Anti AI-Slop Rules

UI HARUS menghindari pola desain generik berikut:

```text
❌ Giant gradient hero
❌ Glassmorphism
❌ Floating blobs
❌ Background mesh gradient
❌ Excessive rounded cards
❌ Every section inside a card
❌ Huge decorative illustrations
❌ Random 3D icons
❌ Fake statistics
❌ Excessive shadow
❌ Excessive animation
❌ Excessive pills
❌ Dashboard with meaningless charts
❌ "AI-looking" futuristic visuals
❌ Overuse of emojis
```

### Jangan melakukan ini

```text
┌──────────────────────────────┐
│   ✨ EARTHQUAKE ALERT ✨     │
│                              │
│   [ giant gradient card ]    │
│                              │
│   [ glass card ] [ glass ]   │
│                              │
│        ◉ 98% SAFE            │
│                              │
└──────────────────────────────┘
```

### Gunakan pendekatan ini

```text
Earthquake Alert

● Protection active

──────────────────────────────

No active alert

Your area is being checked for
relevant earthquake activity.

──────────────────────────────

Latest earthquake
M 4.2 · 126 km away
Central Java · 16:32
```

---

# 3. Design Philosophy

## Normal state

Normal state harus terasa tenang.

Tujuannya:

> Jangan membuat user merasa sedang berada dalam kondisi darurat ketika tidak ada gempa.

Gunakan:

- neutral background
- dark text
- subtle borders
- small status indicator
- generous but controlled whitespace

## Alert state

Ketika gempa relevan:

> Information density increases, decoration decreases.

Gunakan:

- strong contrast
- large typography
- emergency color
- minimal navigation
- prominent action

---

# 4. Color System

Gunakan semantic color system.

## Light Theme

```text
Background        #F8F8F6
Surface           #FFFFFF
Text Primary      #111111
Text Secondary    #656565
Border            #E4E4E0
Muted              #949494

Emergency         #C62828
Emergency Dark    #8E1B1B

Warning           #B77900
Success           #2E7D32
Info              #1769AA
```

## Dark Theme

```text
Background        #101010
Surface           #181818
Text Primary      #F5F5F2
Text Secondary    #A5A5A0
Border            #30302E
Muted              #777772

Emergency         #FF5A5A
Emergency Dark    #D93636

Warning           #E5B03B
Success           #63C174
Info              #64A8E0
```

### Color rules

Normal screens:

- use neutral colors
- semantic colors only for status

Emergency screen:

- emergency red is allowed as the dominant accent
- don't turn every element red
- retain neutral text where contrast is better

Never use gradients as a primary visual language.

---

# 5. Typography

Use Android system typography where possible.

Preferred:

```text
Primary font: Roboto / system sans
```

Do not introduce a custom font unless there is a compelling reason.

## Type scale

```text
Display Large       36sp / Bold
Display Medium      30sp / Bold
Headline Large      26sp / Bold
Headline Medium     22sp / SemiBold
Headline Small      18sp / SemiBold

Title Large         17sp / SemiBold
Title Medium        15sp / Medium

Body Large          16sp / Regular
Body Medium         14sp / Regular
Body Small          12sp / Regular

Label Large         14sp / Medium
Label Medium        12sp / Medium
Label Small         11sp / Medium
```

## Emergency typography

Magnitude:

```text
48sp – 64sp
Bold
```

Alert title:

```text
24sp – 30sp
Bold
```

Distance:

```text
18sp – 22sp
SemiBold
```

Supporting information:

```text
14sp
```

---

# 6. Spacing System

Use an 8dp base grid.

```text
4dp    Micro
8dp    XS
12dp   Small
16dp   Medium
24dp   Large
32dp   XL
48dp   XXL
64dp   Emergency spacing
```

### General rule

Prefer fewer, larger spacing decisions rather than many tiny spacing values.

---

# 7. Shape System

The app should not look like a collection of floating cards.

## Corner radius

```text
Small controls       8dp
Buttons              10dp
Cards                12dp
Large containers      16dp
```

Avoid:

```text
32dp / 40dp / 50dp
```

for everything.

Large pill shapes should only be used for compact status indicators when useful.

---

# 8. Elevation & Shadows

Default:

```text
No shadow
```

Use borders instead.

Elevation only when necessary for:

- modal
- bottom sheet
- dialog
- floating action element

Avoid heavy shadows.

---

# 9. Iconography

Use Material Symbols / Android system icons.

Preferred style:

```text
Outlined / simple
```

Icons should communicate function.

Examples:

```text
Location        location_on
History         history
Settings        settings
Warning         warning
Emergency       emergency
Refresh         refresh
Notifications   notifications
Volume          volume_up
Vibration       vibration
```

Do not use random decorative icons.

---

# 10. Navigation

Use a simple bottom navigation.

## Navigation destinations

```text
Home
History
Settings
```

Example:

```text
┌────────────────────────────────────┐
│                                    │
│             CONTENT                │
│                                    │
│                                    │
├────────────────────────────────────┤
│   Home       History       Settings│
└────────────────────────────────────┘
```

Active destination should be clearly identifiable without relying only on color.

Emergency state may temporarily take over navigation.

---

# 11. Screen Architecture

Primary screens:

```text
Onboarding
Home
Active Alert
Earthquake Detail
History
Settings
```

Navigation:

```text
Onboarding
    ↓
Home
 ├── History
 │    └── Detail
 │
 └── Settings

Earthquake Alert
    ↓
Detail
```

---

# 12. Onboarding Design

## Screen 1

```text
Earthquake Alert

Know when an earthquake
may affect your area.

[ Get Started ]
```

No illustration required.

Optional small monochrome earthquake icon is acceptable.

## Screen 2 — Location

```text
Your location helps us
determine which earthquakes
are relevant to you.

[ Allow Location ]
```

Explain permission before triggering system permission dialog.

## Screen 3 — Notifications

```text
Emergency alerts

Receive notifications,
sound and vibration for
relevant earthquakes.

[ Enable Notifications ]
```

## Screen 4 — Finish

```text
You're ready.

Earthquake Alert is active
and checking for relevant
earthquake activity.

[ Continue ]
```

---

# 13. Home Screen

Home should be the calmest screen.

## Layout

```text
┌────────────────────────────────────┐
│ Earthquake Alert                   │
│                                    │
│ ● Protection active                │
│                                    │
│ Location                           │
│ Yogyakarta                         │
│ Updated 2 min ago                  │
│                                    │
│ ────────────────────────────────   │
│                                    │
│ No active alert                    │
│                                    │
│ Your area is being checked for     │
│ relevant earthquake activity.      │
│                                    │
│ ────────────────────────────────   │
│                                    │
│ Latest earthquake                  │
│                                    │
│ M 4.2                              │
│ 126 km away · Central Java         │
│ 16:32 WIB                          │
│                                    │
│ View details →                     │
│                                    │
└────────────────────────────────────┘
```

### Home hierarchy

1. Protection status
2. Current location
3. Active alert status
4. Latest relevant earthquake
5. Navigation

---

# 14. Home — Active State

When there is an active relevant alert:

```text
┌────────────────────────────────────┐
│ EARTHQUAKE DETECTED                │
│                                    │
│ M 5.4                              │
│                                    │
│ 32 km away                         │
│ Depth 10 km                        │
│ Southern Java                      │
│                                    │
│ [ View Alert ]                     │
│                                    │
│ ⚠ Protect yourself.                │
└────────────────────────────────────┘
```

Emergency section should visually dominate the screen.

---

# 15. Active Alert Screen

This is the most important screen in the application.

## Goal

User must understand the event within 1–2 seconds.

## Layout

```text
┌────────────────────────────────────┐
│                                    │
│              WARNING               │
│                                    │
│       EARTHQUAKE DETECTED          │
│                                    │
│              M 5.4                 │
│                                    │
│           32 km away               │
│                                    │
│      Southern Java · 10 km         │
│                                    │
│ ────────────────────────────────   │
│                                    │
│ Protect your head.                 │
│ Move away from glass and objects   │
│ that may fall.                     │
│                                    │
│ [ View Details ]                   │
│                                    │
│ Dismiss                            │
└────────────────────────────────────┘
```

## Rules

- no bottom navigation
- no decorative image
- no map as primary element
- magnitude is visually dominant
- distance comes second
- location/depth are supporting information
- only one major action

---

# 16. Alert Animation

Animation should communicate urgency, not decoration.

Allowed:

- subtle screen transition
- controlled pulse on emergency indicator
- short entrance animation
- vibration

Avoid:

```text
❌ bouncing UI
❌ infinite scale animations
❌ rotating icons
❌ flashing entire screen continuously
❌ confetti
❌ complex particle effects
```

The emergency alert must remain readable even when animation is disabled.

---

# 17. Earthquake Detail Screen

## Header

```text
Earthquake

M 5.4
Southern Java
```

## Main information

Use a simple information layout instead of a dashboard grid.

```text
Magnitude          5.4
Depth               10 km
Distance            32 km
Time                18:32 WIB

Location
Southern Java

Coordinates
7.82° S
110.42° E

Tsunami
No tsunami potential

Source
BMKG
```

No chart is required in MVP.

---

# 18. History Screen

## Header

```text
History
```

## List

```text
Today

M 4.8
56 km away
Yogyakarta
17:42 WIB

M 3.9
124 km away
Central Java
14:21 WIB


Yesterday

M 5.1
82 km away
South Java
21:14 WIB
```

Use dividers.

Avoid putting every item into a giant card.

---

# 19. History List Item

Preferred:

```text
M 4.8
Yogyakarta

56 km away                         17:42
```

Use magnitude as the strongest visual element.

Optional semantic indicator:

```text
● Alerted
```

Do not use unnecessary badges everywhere.

---

# 20. Empty History

```text
No earthquake history yet.

Relevant earthquake events
will appear here.
```

Do not show fake sample data in production.

---

# 21. Settings Screen

Use grouped sections.

```text
Settings

Alerts
────────────────────────────────
Earthquake alerts                 ON
Strong earthquake alerts          ON
Felt earthquake alerts            ON

Emergency behavior
────────────────────────────────
Sound                             ON
Vibration                         ON
Full-screen alert                 ON
Preview emergency alarm           →

Location
────────────────────────────────
Location permission               Allowed
Location status                   Active

System
────────────────────────────────
Notification settings             →
Critical alert setup              →

About
────────────────────────────────
Data source                       BMKG
Version                           1.0.0
```

Keep settings flat and easy to scan.

---

# 22. Settings Controls

Use native Android controls:

- Switch
- List item
- Slider only where necessary
- Dialog for advanced configuration

Do not create custom toggles unless the default component cannot satisfy the visual requirement.

---

# 23. Emergency Alarm Setup

Dedicated setup page:

```text
Emergency alerts

For urgent earthquakes, Earthquake
Alert can use sound, vibration and
full-screen notifications.

Sound                       ON
Vibration                   ON
Full-screen alert           ON

[ Test Emergency Alert ]

System permissions

Notifications               Allowed
Do Not Disturb access       Check
```

The screen must clearly distinguish:

```text
App setting
```

from:

```text
Android system permission
```

---

# 24. Button Design

## Primary button

```text
Height: 48–52dp
Horizontal padding: 20dp
Radius: 10dp
```

Example:

```text
┌───────────────────────────────┐
│       View Earthquake         │
└───────────────────────────────┘
```

Primary action should be visually obvious.

## Secondary action

Use:

- text button
- outlined button

Avoid multiple filled buttons competing for attention.

---

# 25. Status Indicator

Protection state:

```text
● Protection active
```

Possible states:

```text
● Active
○ Updating
! Limited
× Disabled
```

Use text plus icon/state, not color alone.

---

# 26. Data Freshness

Whenever relevant:

```text
Updated 2 min ago
```

or:

```text
Last checked 18:32 WIB
```

For stale data:

```text
Last updated 25 min ago
Data may be outdated.
```

This builds trust.

---

# 27. Error UI

## Network

```text
Unable to update earthquake data.

Showing your latest saved information.

[ Try Again ]
```

## Location

```text
Location unavailable.

Earthquake relevance may be limited.

[ Open Location Settings ]
```

## Notification

```text
Emergency notifications are disabled.

Enable notifications in Android settings
to receive alerts.

[ Open Settings ]
```

---

# 28. Loading UI

Keep it simple.

Example:

```text
Updating earthquake data...
```

Use a small progress indicator.

Do not use elaborate skeleton layouts for short operations.

---

# 29. Toast / Snackbar

Use Snackbar for lightweight information:

```text
Location updated
```

```text
Earthquake data updated
```

```text
Alarm settings saved
```

Do not use Snackbar for critical emergency information.

---

# 30. Emergency Sound UX

The visual design and auditory design must match.

When an alert triggers:

```text
Sound
+
Vibration
+
Visual emergency state
```

When user opens the alert:

```text
Alarm continues
       ↓
User acknowledges
       ↓
Alarm stops
```

Do not auto-dismiss the visual alert immediately.

---

# 31. Accessibility

Requirements:

- minimum 48dp touch targets,
- readable contrast,
- support Android font scaling,
- content descriptions for icons,
- TalkBack-friendly labels,
- emergency content readable without animation,
- color not used as the only indicator.

Example:

Do not communicate:

```text
RED = emergency
GREEN = safe
```

Only.

Instead:

```text
⚠ Earthquake detected
```

---

# 32. Dark Mode

Support Android system theme.

## Dark mode principles

- don't use pure #000000 everywhere,
- use near-black surfaces,
- keep emergency red bright enough for contrast,
- reduce border visibility but retain structure,
- avoid glowing neon effects.

Example:

```text
Background #101010
Surface    #181818
Text       #F5F5F2
Secondary  #A5A5A0
```

---

# 33. Motion Design

Global rule:

**Motion should clarify state changes, never decorate.**

Allowed:

```text
Screen fade
Small slide transition
Alert entrance
Status pulse
Snackbar transition
```

Duration:

```text
Fast     120–180ms
Normal   200–300ms
Emergency entrance <= 300ms
```

Avoid long transitions.

---

# 34. Responsive Layout

Primary target:

```text
Android phones
```

Support different screen sizes.

Do not hardcode positions based on a single phone.

Use:

- Column
- Row
- Box
- LazyColumn
- WindowInsets
- adaptive padding

Content should remain usable on:

- small phone
- large phone
- portrait
- landscape where appropriate

---

# 35. Density Guidelines

Use Compose `dp` and `sp`.

Never hardcode pixel values.

Avoid:

```kotlin
width = 412
```

Prefer:

```kotlin
fillMaxWidth()
padding(horizontal = 16.dp)
```

---

# 36. Design Tokens

Create a centralized theme/token system.

Suggested:

```kotlin
object AppSpacing {
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}
```

Similarly centralize:

```text
AppColors
AppTypography
AppShapes
```

Do not scatter visual constants throughout the project.

---

# 37. Component Library

Create reusable components only where repetition exists.

Suggested components:

```text
ProtectionStatus
EarthquakeSummary
EarthquakeListItem
SeverityLabel
PrimaryButton
SecondaryButton
SettingsSection
SettingsItem
PermissionStatusItem
EmergencyHeader
InfoRow
```

Do not create a component for every tiny layout.

---

# 38. Recommended Home Components

```text
HomeScreen
├── AppHeader
├── ProtectionStatus
├── LocationSummary
├── AlertStatus
├── LatestEarthquake
└── BottomNavigation
```

---

# 39. Recommended Alert Components

```text
ActiveAlertScreen
├── EmergencyHeader
├── MagnitudeDisplay
├── DistanceDisplay
├── EarthquakeMeta
├── SafetyMessage
└── PrimaryAction
```

---

# 40. Information Hierarchy

Always prioritize:

```text
1. Is this an emergency?
2. How strong is it?
3. How close is it?
4. Where did it happen?
5. What should I do?
6. Additional data
```

Never put:

```text
Coordinates
API source
Last update
```

above:

```text
EARTHQUAKE DETECTED
M 5.4
32 km away
```

---

# 41. Copywriting Style

Use short, direct sentences.

### Good

```text
Earthquake detected.
32 km away.

Protect your head.

Move away from windows.
```

### Bad

```text
Congratulations! We've detected an earthquake
near your current geographic coordinates with an
estimated seismic intensity that might potentially
impact your surrounding environment.
```

Never use marketing language during emergency states.

---

# 42. Microcopy

## Normal

```text
No active alert
Protection active
Location updated 2 min ago
```

## Warning

```text
Earthquake detected
```

## Emergency

```text
Take action now.
Protect your head.
Move away from glass.
```

## System limitation

```text
Emergency alert access is limited by Android.
Check system settings.
```

Avoid fear-inducing copy such as:

```text
🚨 YOU ARE IN EXTREME DANGER!!!
```

unless scientifically justified by the event.

---

# 43. Source Trust

Show source consistently but unobtrusively:

```text
Source: BMKG
```

Use:

```text
Provided by BMKG
```

where appropriate.

Do not visually imply official government ownership of the app.

---

# 44. No Fake Data

Production UI must never display:

```text
98% Safe
2.4M users protected
12 earthquakes prevented
AI confidence 97%
```

unless those metrics actually exist and have a valid definition.

---

# 45. Map Usage

Map is NOT the primary screen for MVP.

If added later:

- use it only to explain epicenter/location,
- keep markers simple,
- avoid map-heavy dashboards,
- don't show unnecessary heatmaps.

Priority remains textual information.

---

# 46. Empty / Normal State Philosophy

Normal state is not a "dashboard".

It is a status page.

Primary message:

```text
No active alert
```

Secondary explanation:

```text
Your area is being checked for
relevant earthquake activity.
```

Then show the latest event.

---

# 47. Emergency State Philosophy

Emergency screen is not a dashboard either.

It is a **single-purpose action screen**.

Primary content:

```text
EARTHQUAKE DETECTED
M 5.4
32 km away
```

Secondary content:

```text
Protect your head.
Move away from windows.
```

One primary action:

```text
View details
```

---

# 48. Android System Integration UI

The app should respect Android conventions.

Use native:

- permission dialogs,
- notification settings,
- DND settings,
- system back behavior,
- status/navigation bar handling.

Never recreate a fake Android settings interface.

---

# 49. Xiaomi / OEM Considerations

Because Android manufacturers may modify background execution and notification behavior:

The app should include a lightweight troubleshooting section:

```text
Not receiving alerts?

1. Make sure notifications are enabled.
2. Allow Earthquake Alert to run in background.
3. Check battery optimization settings.
4. Check Do Not Disturb / notification policy access.
```

Do not assume every Android vendor behaves identically.

---

# 50. UI State Matrix

| Screen | Normal | Loading | Error | Emergency |
|---|---|---|---|---|
| Home | calm | subtle spinner | retry state | emergency banner |
| History | list | spinner | retry state | no emergency takeover needed |
| Detail | data | spinner | unavailable | warning emphasis |
| Settings | normal | minimal | inline error | system guidance |
| Alert | hidden | n/a | n/a | full emergency UI |

---

# 51. Design QA Checklist

Before considering a screen finished:

### Visual

- [ ] No unnecessary gradient.
- [ ] No excessive cards.
- [ ] No excessive rounded pills.
- [ ] Typography hierarchy is obvious.
- [ ] Spacing uses design tokens.
- [ ] Icons have clear purpose.
- [ ] No decorative UI competes with important information.

### UX

- [ ] Primary action is obvious.
- [ ] Important information is visible without scrolling where possible.
- [ ] Error states are understandable.
- [ ] Loading states are short and simple.
- [ ] Emergency state is instantly recognizable.

### Accessibility

- [ ] Touch targets are large enough.
- [ ] Text can scale.
- [ ] Contrast is sufficient.
- [ ] Meaning does not depend only on color.
- [ ] Icons have content descriptions.

---

# 52. Vibe Coding Instructions

When an AI coding agent implements the UI:

1. Read this entire `DESIGN.md` before creating UI.
2. Treat this file as the design source of truth.
3. Prefer native Jetpack Compose components.
4. Reuse design tokens.
5. Build reusable components only for actual repetition.
6. Keep screens visually simple.
7. Do not invent gradients or decorative illustrations.
8. Do not introduce random colors.
9. Do not redesign screens without updating this file.
10. Prioritize information hierarchy over visual novelty.
11. Test light and dark mode.
12. Test small and large phone sizes.
13. Test large accessibility font sizes.
14. Test emergency screen with reduced animations.
15. Keep emergency content readable at a glance.

---

# 53. Implementation Priority

Build UI in this order:

```text
1. Theme
2. Typography
3. Spacing
4. Buttons
5. Status components
6. Home
7. History
8. Detail
9. Settings
10. Onboarding
11. Emergency Alert
12. Error states
13. Dark mode refinement
14. Accessibility refinement
```

Important:

**Build the emergency screen early enough to validate the core UX, but do not postpone the base design system until the end.**

---

# 54. Final Design Principle

The application should feel like:

> **a trustworthy emergency utility that happens to have a modern interface.**

Not:

> **a flashy modern interface that happens to show earthquake data.**
