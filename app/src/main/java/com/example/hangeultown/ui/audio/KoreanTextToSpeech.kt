package com.example.hangeultown.ui.audio

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Stable
class KoreanSpeaker internal constructor() {
  private var engine: TextToSpeech? = null

  var isReady by mutableStateOf(false)
    private set

  internal fun attach(engine: TextToSpeech, status: Int) {
    this.engine = engine
    if (status != TextToSpeech.SUCCESS) {
      isReady = false
      return
    }
    val languageResult = engine.setLanguage(Locale.KOREAN)
    isReady = languageResult != TextToSpeech.LANG_MISSING_DATA &&
      languageResult != TextToSpeech.LANG_NOT_SUPPORTED
  }

  fun speak(text: String) {
    if (!isReady || text.isBlank()) return
    engine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "hangeul-town-$text")
  }

  internal fun release(fallbackEngine: TextToSpeech? = null) {
    val engineToRelease = engine ?: fallbackEngine
    engineToRelease?.stop()
    engineToRelease?.shutdown()
    engine = null
    isReady = false
  }
}

@Composable
fun rememberKoreanSpeaker(): KoreanSpeaker {
  val appContext = LocalContext.current.applicationContext
  val speaker = remember(appContext) { KoreanSpeaker() }

  DisposableEffect(appContext, speaker) {
    var disposed = false
    var released = false
    var createdEngine: TextToSpeech? = null
    createdEngine = TextToSpeech(appContext) { status ->
      val engine = createdEngine
      if (disposed && !released) {
        engine?.shutdown()
        released = true
      } else if (engine != null) {
        speaker.attach(engine, status)
      }
    }

    onDispose {
      disposed = true
      if (!released) {
        speaker.release(createdEngine)
        released = true
      }
    }
  }

  return speaker
}
