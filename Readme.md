# Voice Assistant (Personal Build)

A push-to-talk voice assistant for Android that handles 3 commands via a Quick Settings tile. Built for personal/sideload use only — not distributed on Play Store.

## What it does

| Say | Action |
|---|---|
| "call mom" | Looks up the contact and places the call directly |
| "set alarm for 7 30" | Opens your default clock app's alarm screen, pre-filled |
| "play blinding lights on spotify" | Plays it via the Spotify app (first login will ask you to authorize) |

No wake word — you trigger it manually via a Quick Settings tile. No support for WhatsApp, banking apps, or anything outside these 3 commands.

## Why it's built this way (things it deliberately does NOT do)

- **No always-listening mic.** Push-to-talk only — no background service, no battery drain, no persistent notification.
- **No accessibility service / screen automation.** Every action uses a real Android API (`ACTION_CALL`, `ACTION_SET_ALARM`, Spotify's official App Remote SDK) — nothing simulates taps or reads other apps' screens. This is why it's stable across app updates and why it can't touch apps that don't expose these APIs (like WhatsApp).
- **Can't access banking apps / Groww by design, not by choice.** Financial apps block this category of access themselves (`FLAG_SECURE`) — there's nothing here to configure around that.

## Requirements

- Android 8.0 (API 26) or higher
- A phone with Google's Speech Recognizer available (standard on any device with Google apps)
- Spotify app installed and logged in, if you want the Spotify command to work
- Android Studio, to build and install (see below)

## Setup

1. **Spotify Client ID** — register a free app at [developer.spotify.com/dashboard](https://developer.spotify.com/dashboard), add redirect URI `voiceassistant://callback`, copy your Client ID into `Actions.kt`:
   ```kotlin
   private const val CLIENT_ID = "YOUR_SPOTIFY_CLIENT_ID"
   ```
2. **Open in Android Studio** as a new project, drop in the 5 `.kt` files, `AndroidManifest.xml`, and `build.gradle.kts` as given.
3. **Build & install** — connect your phone via USB with Developer Options + USB Debugging enabled, hit Run in Android Studio. No Play Store, no signing required for personal install.
4. **Grant permissions** — open the app once; it will request Microphone, Call, and Contacts permissions together.
5. **Add the tile** — swipe down twice on your phone → tap Edit (pencil icon) → drag "Voice Assistant" into your active tiles.

## Using it

Swipe down → tap the tile → speak your command. If it doesn't understand, it'll show a toast and you try again — nothing fires silently on a bad match.

## Known limitations

- **WhatsApp is not supported.** No public API exists to send a message without a manual tap — this is Meta's anti-spam design, not a missing feature here.
- **OEM speech recognizer quirks.** Some Android skins (MIUI, some Samsung builds) show their own floating mic UI instead of listening silently. If that happens, it's the phone's default assist app intercepting the request — fix via Settings → Apps → Default apps → Digital assistant, not a code change.
- **Contact match is "contains" matching**, not exact — "call mom" matches the first contact whose name contains "mom". Fine for a personal phonebook, will misfire on ambiguous names.

## Project structure

```
app/
├── build.gradle.kts
└── src/main/
    ├── AndroidManifest.xml
    └── java/com/alpha/voiceassistant/
        ├── MainActivity.kt           — permission requests, launcher screen
        ├── SpeechRecognizerHelper.kt — mic in, text out
        ├── CommandMatcher.kt         — text in, Action out
        ├── Actions.kt                — executes Call / Alarm / Spotify
        └── TriggerTileService.kt     — Quick Settings tile, wires it together
```
