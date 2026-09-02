package com.example.hangeultown.domain.hangul

import org.junit.Assert.assertEquals
import org.junit.Test

class HangulComposerTest {
  @Test fun `giyeok plus a composes ga`() = assertComposition("ㄱ", "ㅏ", expected = "가")

  @Test fun `nieun plus a composes na`() = assertComposition("ㄴ", "ㅏ", expected = "나")

  @Test fun `hieut plus a plus nieun composes han`() =
    assertComposition("ㅎ", "ㅏ", "ㄴ", expected = "한")

  @Test fun `giyeok plus eu plus rieul composes geul`() =
    assertComposition("ㄱ", "ㅡ", "ㄹ", expected = "글")

  @Test fun `siot plus a plus nieun composes san`() =
    assertComposition("ㅅ", "ㅏ", "ㄴ", expected = "산")

  @Test fun `mieum plus u plus rieul composes mul`() =
    assertComposition("ㅁ", "ㅜ", "ㄹ", expected = "물")

  @Test fun `bieup plus a plus bieup composes bap`() =
    assertComposition("ㅂ", "ㅏ", "ㅂ", expected = "밥")

  @Test fun `delete removes final then medial then initial`() {
    val composer = HangulComposer().apply { listOf("ㅎ", "ㅏ", "ㄴ").forEach(::input) }
    composer.delete()
    assertEquals("하", composer.text)
    composer.delete()
    assertEquals("ㅎ", composer.text)
    composer.delete()
    assertEquals("", composer.text)
  }

  @Test fun `final moves to next syllable when vowel follows`() {
    val composer = HangulComposer().apply { listOf("ㅅ", "ㅏ", "ㄱ", "ㅘ").forEach(::input) }
    assertEquals("사과", composer.text)
  }

  @Test fun `two beolsik key sequence composes sagwa`() {
    val composer = HangulComposer().apply {
      listOf("ㅅ", "ㅏ", "ㄱ", "ㅗ", "ㅏ").forEach(::input)
    }
    assertEquals("사과", composer.text)
  }

  private fun assertComposition(vararg input: String, expected: String) {
    val composer = HangulComposer()
    input.forEach(composer::input)
    assertEquals(expected, composer.text)
  }
}
