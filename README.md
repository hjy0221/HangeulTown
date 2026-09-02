# Hangeul Town

Hangeul Town is a smartphone Android learning game where foreign learners discover how Hangul syllables are assembled, then practice real Korean typing through short game stages.

The first playable slice focuses on:

- real-time Hangul jamo composition
- a two-beolsik-style in-app Korean keyboard
- syllable merge animation feedback
- starter word challenges with emoji, English, and romanization hints
- XP, combo, stage clear rewards
- Korean pronunciation playback through Android TextToSpeech

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Android ViewModel and StateFlow
- Android TextToSpeech

## Current Stage

The current starter stage teaches:

- 가
- 나
- 사과
- 우유
- 커피
- 바나나

The stage clear target currently reaches 950 XP and a 6 combo streak.

## Build

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

To run connected Android tests on a smartphone emulator:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```
