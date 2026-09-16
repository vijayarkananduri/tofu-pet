# Tofu Pet — Android Application

**A cream-colored pill-shaped pet for Android that floats over your screen, reacts to touch, tilt, shake, and voice, and gently reminds you of your tasks.**

## Overview

Tofu is a small, warm creature living on your Android screen. It has two eyes and a mouth. It gets happy when you do your tasks, annoyed when you poke too much, dizzy when you shake the phone, and distant when you've been slacking. It never nags with numbers or streaks. It just **feels**.

## Features

- ✨ **Floating overlay pet** that lives on your Android homescreen
- 👁️ **Responsive to gestures**: tap, double-tap, long press, drag, and swipe
- 📱 **Sensor-aware**: responds to shake, tilt, and spin via gyro and accelerometer
- 🎤 **Voice control**: offline keyword-based voice commands (no AI/LLM)
- 🔔 **Smart reminders**: gentle mood-based task reminders with snooze ladder
- 💾 **Persistent state**: all settings and mood saved across sessions
- 🔇 **Mute gesture**: swipe across mouth to toggle sound
- 🎨 **Fully customizable**: adjust sensitivity, pet size, snooze time, and more
- 📦 **Zero AI dependency**: fully offline, no external services required

## Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin |
| **Main UI** | Jetpack Compose |
| **Pet UI** | HTML/CSS/JS in transparent WebView |
| **Overlay** | `WindowManager` + `TYPE_APPLICATION_OVERLAY` |
| **Sensors** | `SensorManager` (gyro + accelerometer) |
| **Speech** | `SpeechRecognizer` (offline) + Android `TextToSpeech` |
| **Storage** | DataStore (Preferences) |
| **Scheduling** | `AlarmManager` + `WorkManager` |
| **Minimum SDK** | Android 9 (API 28) |
| **Target SDK** | Android 14 (API 34) |

## Project Structure

```
tofu-pet/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/tofu/pet/
│   │   │   │   ├── MainActivity.kt                # Entry point with onboarding
│   │   │   │   ├── SettingsActivity.kt            # Settings UI
│   │   │   │   ├── overlay/
│   │   │   │   │   └── PetOverlayService.kt       # Main overlay service
│   │   │   │   ├── reminder/
│   │   │   │   │   ├── ReminderController.kt      # Alarm scheduling
│   │   │   │   │   └── ReminderReceiver.kt        # Alarm broadcast receiver
│   │   │   │   ├── voice/
│   │   │   │   │   └── VoiceController.kt         # STT + TTS integration
│   │   │   │   ├── data/
│   │   │   │   │   └── PreferencesRepository.kt   # DataStore layer
│   │   │   │   ├── model/
│   │   │   │   │   └── Models.kt                  # Data classes
│   │   │   │   ├── util/
│   │   │   │   │   ├── PetResponses.kt            # Mood-based replies
│   │   │   │   │   └── SensorUtils.kt             # Sensor calculations
│   │   │   │   └── ui/theme/
│   │   │   │       └── Theme.kt                   # Compose theme
│   │   │   ├── assets/
│   │   │   │   ├── tofu.html                      # Pet UI
│   │   │   │   ├── tofu.css                       # Pet styling
│   │   │   │   └── tofu.js                        # Pet state machine
│   │   │   ├── res/
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── xml/
│   │   │   │       ├── data_extraction_rules.xml
│   │   │   │       └── backup_rules.xml
│   │   │   └── AndroidManifest.xml
│   │   └── proguard-rules.pro
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Getting Started

### Prerequisites

- Android Studio Giraffe (2022.3.1) or newer
- Android SDK 34 (API level 34)
- Kotlin 1.9.10+
- Gradle 8.1+

### Build & Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/vijayarkananduri/tofu-pet.git
   cd tofu-pet
   ```

2. **Open in Android Studio**
   ```bash
   open -a "Android Studio" .
   ```

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run on an emulator or device**
   ```bash
   ./gradlew installDebug
   ```
   Or use Android Studio's **Run** button (Shift+F10).

### First Launch

1. Launch the app from your home screen
2. Follow the onboarding screens to grant:
   - **Display over other apps** (required for overlay)
   - **Microphone** (required for voice commands)
   - **Notifications** (recommended for reminders)
3. Tap **Launch Pet Overlay** to see Tofu on your screen
4. Customize settings as needed

## Usage

### Gestures

| Gesture | Action |
|---|---|
| **Single tap** | Poke Tofu (blink + bounce) |
| **Double tap** | Poke harder (wiggle mouth) |
| **Triple+ tap** | Annoyed mood (3s) |
| **Long press** | Activate microphone for voice commands |
| **Swipe eyes** | Show clock for 3 seconds |
| **Swipe mouth** | Toggle mute (zip/unzip mouth) |
| **Drag** | Move Tofu around the screen |
| **Shake (1-5)** | Angry mood |
| **Shake (6-10)** | Dizzy mood |
| **Spin ×2** | Dizzy mood |

### Voice Commands

Long press to activate, then say:

| Command | Action |
|---|---|
| "hello" / "hi" | Tofu waves |
| "how many" / "what's up" | Shows task count |
| "show me" / "open" | Opens the app |
| "done" | Checks off current task |
| "done one/two/three" | Checks specific task by number |
| "snooze" | Snoozes current task (5 min default) |
| "snooze one/two/three" | Snoozes specific task |
| "what's task one" / "read one" | Reads task aloud |
| "mute" / "shush" | Mutes Tofu |
| "unmute" / "talk" | Unmutes Tofu |

### Settings

- **Tilt Sensitivity**: How responsive Tofu is to phone tilt
- **Shake Sensitivity**: How easily Tofu gets angry or dizzy
- **Pet Size**: Scale Tofu from 50% to 200%
- **Your Name**: Personalize Tofu's messages (optional)
- **Default Snooze Time**: 5, 10, 15, or 30 minutes
- **Sound Enabled**: Toggle all sound reactions
- **Muted**: Accessibility toggle for swipe-based mute
- **Auto-mute on Silent**: Automatically silence when phone is silent

## Mood System

Tofu's behavior is determined by an invisible **mood score** (-3 to +3) and **trust score** (0 to 3):

### Mood Levels

| Score | Level | Behavior |
|---|---|---|
| ≥ +2 | **Warm** | Blush on, waves, occasional ♥ eyes, soft replies |
| -1 to +1 | **Neutral** | Default idle, small reactions |
| ≤ -2 | **Cold** | Half-closed eyes, flat mouth, short replies |

### Mood Changes

- **+1**: Task completed on time
- **0**: Task completed after 1 snooze
- **-1**: Task completed after 3+ snoozes or task skipped
- **-2**: Task missed entirely
- **+1 partial**: Drift toward neutral each morning

### Trust Score & Snooze Ladder

Higher trust = more snoozes allowed per task:

| Trust | Max Snoozes | Snooze Cap Message |
|---|---|---|
| 3 | 3 | "This is the LAST time." |
| 1 | 1 | "Fine... but hurry." |
| 0 | 0 | (No snooze button) |

## Reminders

1. **Reminder fires**: Pet glows with cute alarm sound
2. **Tap or voice command**: Check off task instantly, or tap bubble for options
3. **Snooze**: Reschedules for default snooze time (configurable)
4. **No response (5 min)**: Pet goes cold, stops looking at you
5. **Miss (60 min)**: Pet becomes grumpy, trust score drops

## Architecture

### Kotlin ↔ JS Bridge

**Kotlin → JavaScript** (sent via `WebView.evaluateJavascript()`)
```kotlin
window.Pet.onTap()
window.Pet.onShake(level: Int)
window.Pet.onSpin()
window.Pet.onTilt(x: Float, y: Float)
window.Pet.onMood(mood: String)  // "warm" | "neutral" | "cold"
window.Pet.onReminder(taskId: String, title: String, count: Int)
window.Pet.onMuteChanged(muted: Boolean)
window.Pet.onUserName(name: String)
window.Pet.onIdle()
```

**JavaScript → Kotlin** (via `AndroidBridge` interface)
```javascript
AndroidBridge.vibrate(ms: Long)
AndroidBridge.reportBounds(x: Int, y: Int, w: Int, h: Int)
AndroidBridge.checkTask(taskId: String)
AndroidBridge.snoozeTask(taskId: String)
AndroidBridge.openApp()
AndroidBridge.requestTts(text: String)
AndroidBridge.openMic()
AndroidBridge.setMuted(muted: Boolean)
```

### State Machine

```
IDLE ──tap──→ POKE ──(800ms)──→ IDLE
IDLE ──long press──→ LISTENING ──(5s silence)──→ IDLE
IDLE ──shake 1-5──→ ANGRY ──(level×0.4s)──→ IDLE
IDLE ──shake 6-10──→ DIZZY ──(level×0.6s)──→ IDLE
IDLE ──reminder──→ GLOW ──tap/done──→ IDLE
GLOW ──snooze──→ SNOOZED ──(snooze time)──→ GLOW
```

## Permissions

| Permission | Purpose | Requested When |
|---|---|---|
| `SYSTEM_ALERT_WINDOW` | Draw overlay over other apps | Onboarding |
| `RECORD_AUDIO` | Voice commands | Onboarding |
| `FOREGROUND_SERVICE` | Keep overlay alive in background | Auto |
| `FOREGROUND_SERVICE_MICROPHONE` (API 34+) | Mic in background | Auto |
| `SCHEDULE_EXACT_ALARM` (API 31+) | Precise reminder timing | Auto |
| `POST_NOTIFICATIONS` (API 33+) | Foreground service notification | Onboarding |
| `VIBRATE` | Haptic feedback | Auto |

## Performance & Battery

- **Sensor polling**: Drops to `SENSOR_DELAY_UI` when idle to save battery
- **Expected battery drain**: <5% per hour with full sensor access
- **Target**: 99%+ crash-free sessions
- **Expected D7 retention**: >30%

## Roadmap (v2+)

- 🤖 AI chat mode with emotion tags (LLM-powered)
- 👯 Multiple pets
- 📬 Pet reacts to notifications
- 🎨 Wallpaper / widget mode
- 🔊 Sound packs
- ☁️ Cloud save & sync
- 🍎 iOS version
- 🎬 Full-screen reminder intent
- ⚙️ Per-task snooze override

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -am 'Add my feature'`)
4. Push to the branch (`git push origin feature/my-feature`)
5. Open a Pull Request

## License

This project is licensed under the **Apache License 2.0** — see [LICENSE](LICENSE) for details.

## Privacy

Tofu is **fully offline**. It does not:
- Send data to external servers
- Require internet connection
- Track or store personal information
- Use any third-party analytics

All voice recognition, task management, and mood tracking happens locally on your device.

## Support

For issues, feature requests, or questions:

- Open a [GitHub Issue](https://github.com/vijayarkananduri/tofu-pet/issues)
- Check [existing discussions](https://github.com/vijayarkananduri/tofu-pet/discussions)

## Credits

Built with ❤️ by [Vijayarka Nanduri](https://github.com/vijayarkananduri)

---

**Tofu — a soft little friend who remembers.** 🍲
