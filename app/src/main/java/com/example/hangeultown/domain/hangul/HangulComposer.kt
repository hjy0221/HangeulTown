package com.example.hangeultown.domain.hangul

/**
 * Small, UI-independent Hangul composer for the in-app learning keyboard.
 *
 * It intentionally focuses on modern two-beolsik jamo used by the game, while supporting
 * compound vowels/finals and moving a final consonant to the next syllable when a vowel follows.
 */
class HangulComposer {
  private val committed = StringBuilder()
  private var initial: Char? = null
  private var medial: Char? = null
  private var final: Char? = null

  val text: String
    get() = committed.toString() + currentText()

  fun input(jamo: String) {
    require(jamo.length == 1) { "Input must be one compatibility jamo." }
    val value = jamo.single()
    when {
      value in MEDIALS -> inputVowel(value)
      value in INITIALS || value in FINALS -> inputConsonant(value)
      else -> error("Unsupported jamo: $value")
    }
  }

  fun delete() {
    when {
      final != null -> final = splitFinal(final!!)?.first
      medial != null -> medial = splitMedial(medial!!)
      initial != null -> initial = null
      committed.isNotEmpty() -> committed.deleteCharAt(committed.lastIndex)
    }
  }

  fun clear() {
    committed.clear()
    initial = null
    medial = null
    final = null
  }

  private fun inputConsonant(value: Char) {
    when {
      initial == null -> initial = value
      medial == null -> {
        commitCurrent()
        initial = value
      }
      final == null && value in FINALS -> final = value
      final != null -> {
        val combined = COMBINED_FINALS[final to value]
        if (combined != null) final = combined
        else {
          commitCurrent()
          initial = value
        }
      }
      else -> {
        commitCurrent()
        initial = value
      }
    }
  }

  private fun inputVowel(value: Char) {
    when {
      initial == null -> {
        committed.append(value)
      }
      medial == null -> medial = value
      final == null -> {
        val combined = COMBINED_MEDIALS[medial to value]
        if (combined != null) medial = combined
        else {
          commitCurrent()
          committed.append(value)
        }
      }
      else -> {
        val oldFinal = final!!
        val split = splitFinal(oldFinal)
        final = split?.first
        commitCurrent()
        initial = split?.second ?: oldFinal
        medial = value
      }
    }
  }

  private fun commitCurrent() {
    committed.append(currentText())
    initial = null
    medial = null
    final = null
  }

  private fun currentText(): String {
    val first = initial ?: return ""
    val middle = medial ?: return first.toString()
    val initialIndex = INITIALS.indexOf(first)
    val medialIndex = MEDIALS.indexOf(middle)
    if (initialIndex < 0 || medialIndex < 0) return "$first$middle"
    val finalIndex = final?.let(FINALS::indexOf)?.plus(1) ?: 0
    val codePoint = HANGUL_BASE + ((initialIndex * MEDIALS.size) + medialIndex) * FINAL_COUNT + finalIndex
    return codePoint.toChar().toString()
  }

  private fun splitMedial(value: Char): Char? =
    COMBINED_MEDIALS.entries.firstOrNull { it.value == value }?.key?.first

  private fun splitFinal(value: Char): Pair<Char, Char>? =
    COMBINED_FINALS.entries.firstOrNull { it.value == value }?.key

  private companion object {
    const val HANGUL_BASE = 0xAC00
    const val FINAL_COUNT = 28
    val INITIALS = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ".toList()
    val MEDIALS = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ".toList()
    val FINALS = "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ".toList()
    val COMBINED_MEDIALS = mapOf(
      ('ㅗ' to 'ㅏ') to 'ㅘ', ('ㅗ' to 'ㅐ') to 'ㅙ', ('ㅗ' to 'ㅣ') to 'ㅚ',
      ('ㅜ' to 'ㅓ') to 'ㅝ', ('ㅜ' to 'ㅔ') to 'ㅞ', ('ㅜ' to 'ㅣ') to 'ㅟ',
      ('ㅡ' to 'ㅣ') to 'ㅢ',
    )
    val COMBINED_FINALS = mapOf(
      ('ㄱ' to 'ㅅ') to 'ㄳ', ('ㄴ' to 'ㅈ') to 'ㄵ', ('ㄴ' to 'ㅎ') to 'ㄶ',
      ('ㄹ' to 'ㄱ') to 'ㄺ', ('ㄹ' to 'ㅁ') to 'ㄻ', ('ㄹ' to 'ㅂ') to 'ㄼ',
      ('ㄹ' to 'ㅅ') to 'ㄽ', ('ㄹ' to 'ㅌ') to 'ㄾ', ('ㄹ' to 'ㅍ') to 'ㄿ',
      ('ㄹ' to 'ㅎ') to 'ㅀ', ('ㅂ' to 'ㅅ') to 'ㅄ',
    )
  }
}
