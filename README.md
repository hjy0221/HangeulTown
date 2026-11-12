# Hangeul Town

[日本語](README.ja.md) · [English](README.md) · [한국어](README.ko.md)

Hangeul Town is a smartphone Android app that allows foreign learners to learn how the Korean alphabet is assembled in a game-like manner and even practice actual Korean typing.

## Screenshot

<p>
  <img src="docs/screenshots/starter-build-na.png" alt="Screen of assembling me in the Hangeul Town starter course" width="260" />
  <img src="docs/screenshots/first-words-apple-perfect.png" alt="Hangeul Town first word apple input success screen" width="260" />
</p>

## Core features

- Hangul combination system that assembles letters in real time according to alphabet input
- Korean keyboard inside the app based on a two-set structure
- Merge animation where letters are combined to form syllables and words
- Beginner word challenge using emojis, English meanings, and Roman alphabet pronunciation hints
- XP, combo, stage clear rewards
- Korean pronunciation playback based on Android TextToSpeech

## Currently playable stages

Currently in the first playable version, you learn the following letters and words:

- Go
- me
- apple
- milk
- coffee
- Banana

Completing the stage to the end will currently net you 950 XP and 6 combos.

## Technology Stack

- Kotlin
- Jetpack Compose
-Material 3
- Android ViewModel and StateFlow
-Android TextToSpeech

## Build

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

To run the Android connectivity test on a smartphone emulator, use the following command:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```
