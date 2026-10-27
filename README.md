# 5D Ab Workout — Galaxy Watch8 Classic (Wear OS)

A standalone **Wear OS** app built with **Kotlin** and **Jetpack Compose for Wear OS** that restores and upgrades the classic **5D Ab Workout** (*Exercício Abdominal 5D* / Keep「五维腹肌」) removed from Samsung Health.

---

## Why "5D"? The 5 Biomechanical Dimensions of the Abs
All exercises and visual indicators in this app are color-coded around the 5 core biomechanical functions of the abdominal wall:

1. **1D • Flexion (`#00E5FF`)** — *Upper & Lower Rectus Abdominis* (Knee Touch Crunch, Half Crunch, Bent-Knee Reverse Crunch, Lying Knee Tuck)
2. **2D • Lateral Flexion (`#00E676`)** — *External & Internal Obliques* (Alternate Heel Touch, Left & Right Side-Lying Crunches)
3. **3D • Rotation (`#FFB300`)** — *Obliques & Rotational Core* (Modified Russian Twist)
4. **4D • Counter-Extension (`#FF5252`)** — *Deep Anti-Arch Core Stability* (Plank Hold, 4-Point Bear Plank, Plank Alternating Leg Lift)
5. **5D • Counter-Lateral Flexion (`#E040FB`)** — *Anti-Tilt Lateral Core Stability* (Left & Right Bent-Knee Side Plank)
6. **REC • Transverse & Fascia Recovery (`#64FFDA`)** — *Deep Core & Cooldown* (Stomach Vacuum Hold, Cobra Abdominal Stretch)

---

## Included Routines & Full Customization

### 1. **5D Ab Classic (Original 10-Set Samsung Health Plan)**
- `2 × 25 reps` **Knee Touch Crunch** (*1D Flexion — Upper Abs*)
- `2 × 30 reps` **Bent-Knee Reverse Crunch** (*1D Flexion — Lower Abs*)
- `2 × 15 reps` **Alternate Heel Touch** (*2D Lateral Flexion — Obliques*)
- `2 × 20 reps` **Modified Russian Twist** (*3D Rotation — Core*)
- `2 × 60 sec` **Plank Hold** (*4D Counter-Extension — Deep Core*)

### 2. **5D Complete Sculpt (3 Rounds • All 5 Axes + Vacuum & Stretch)**
- Combines upper/lower flexion, rotation, **Left & Right Side Planks (5D Counter-Lateral Flexion)**, plank leg lifts, **Stomach Vacuum**, and **Cobra Stretch** between rounds.

### 3. **5D 6-Move Circuit (3 Rounds × 6 Exercises)**
- 3 rounds of Half Crunch (`20`), Lying Knee Tuck (`20`), Left Side Crunch (`15`), Right Side Crunch (`15`), Modified Russian Twist (`20`), and 4-Point Bear Plank (`30s`).

### 4. **Custom Routines & Per-Set Editor**
- Tap any routine on the watch to edit **target reps**, **hold duration**, **auto-pace rep speed** (`1.0s–4.5s/rep`), **rest duration**, or **swap/add/remove exercises** from the 15-move 5D Ab catalog.

---

## Galaxy Watch8 Classic Features

- **Physical Rotating Bezel Support**:
  - **In Menus & Lists**: Smoothly scrolls routines, exercise sets, history, and settings.
  - **During Active Sets**: Rotate the bezel to adjust target reps (`±1 rep`) or plank hold time (`±5s`) on the fly, or switch bezel mode (`Bezel: ±Target` ↔ `Bezel: Skip`) to jump between sets with tactile haptic detents.
  - **During Rest Timers**: Rotate the bezel clockwise/counter-clockwise to add or subtract `±5s` of rest.
- **Hands-Free Floor Workout Cues (Haptics + Audio + TTS Voice Coach)**:
  - No need to crane your neck to look at your watch while lying on the mat:
    - **Auto-Paced Rep Cadence**: Pulses your wrist and counts key rep milestones (`1, 5, 10, Halfway, 3, 2, 1`) at each exercise's natural tempo.
    - **Timed Holds**: 10-second interval pulses, halfway alert, and `3-2-1` countdown.
    - **Rest Announcements**: Announces the next exercise name and target so you can get into position before the set starts.
- **Live Heart Rate, Calories & Samsung Health / Health Connect Sync**:
  - Uses **Wear Health Services (`ExerciseClient`)** + hardware **BioActive `TYPE_HEART_RATE` sensor** for real-time BPM and calorie tracking.
  - Automatically writes completed sessions (`ExerciseSessionRecord`, `HeartRateRecord`, `TotalCaloriesBurnedRecord`) to **Health Connect** and **Samsung Health**.

---

## How to Build & Install on Your Galaxy Watch8 Classic

### Option A: Build in Android Studio or via GitHub Actions
1. **Android Studio**: Open this folder (`5dab`) in Android Studio Ladybug or newer and run the `app` configuration directly onto your paired watch, or run `gradlew assembleDebug`.
2. **GitHub Actions (Zero Local SDK Setup)**: Push this repo to GitHub — [`.github/workflows/build-wear-apk.yml`](.github/workflows/build-wear-apk.yml) automatically builds `FiveDAb-GalaxyWatch8-Debug-APK` and `FiveDAb-GalaxyWatch8-Release-APK`.

### Option B: One-Click Wireless Install Script (`scripts/deploy-watch8.ps1`)
1. On your **Galaxy Watch8 Classic**, go to **Settings → About watch → Software information** and tap **Software version** 7 times to enable **Developer options**.
2. Go to **Settings → Developer options → Wireless debugging**, turn it **ON**, and tap **Pair new device** (make sure your watch is on the same Wi-Fi network as your PC).
3. Run the included PowerShell script (it will automatically download official Android `adb.exe` if you don't have it installed):

```powershell
.\scripts\deploy-watch8.ps1 `
  -PairingIpPort "192.168.1.50:38123" `
  -PairingCode "123456" `
  -WatchIpPort "192.168.1.50:41555" `
  -ApkPath ".\app-debug.apk"
```
