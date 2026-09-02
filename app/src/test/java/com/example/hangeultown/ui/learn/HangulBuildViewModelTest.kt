package com.example.hangeultown.ui.learn

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HangulBuildViewModelTest {
  private val dispatcher = StandardTestDispatcher()

  @Before fun setUp() = Dispatchers.setMain(dispatcher)

  @After fun tearDown() = Dispatchers.resetMain()

  @Test fun `ga awards one hundred xp`() {
    val viewModel = HangulBuildViewModel()
    viewModel.onKeyClick("ㄱ")
    viewModel.onKeyClick("ㅏ")

    assertEquals("가", viewModel.uiState.value.input)
    assertEquals(100, viewModel.uiState.value.score)
    assertTrue(viewModel.uiState.value.isCorrect)
  }

  @Test fun `correct ga advances to na and preserves reward`() = runTest(dispatcher) {
    val viewModel = HangulBuildViewModel(autoAdvanceDelayMillis = 1_000)
    viewModel.onKeyClick("ㄱ")
    viewModel.onKeyClick("ㅏ")

    advanceTimeBy(1_001)

    assertEquals("나", viewModel.uiState.value.target)
    assertEquals("Build na", viewModel.uiState.value.prompt)
    assertEquals("ㄴ + ㅏ", viewModel.uiState.value.goal)
    assertEquals("", viewModel.uiState.value.input)
    assertEquals(100, viewModel.uiState.value.score)
    assertEquals(1, viewModel.uiState.value.combo)
  }

  @Test fun `build lessons advance to hidden apple word game`() = runTest(dispatcher) {
    val viewModel = HangulBuildViewModel(autoAdvanceDelayMillis = 1_000)
    listOf("ㄱ", "ㅏ").forEach(viewModel::onKeyClick)
    advanceTimeBy(1_001)
    listOf("ㄴ", "ㅏ").forEach(viewModel::onKeyClick)

    advanceTimeBy(1_001)

    val state = viewModel.uiState.value
    assertEquals("사과", state.target)
    assertEquals("apple", state.english)
    assertEquals("🍎", state.emoji)
    assertEquals("Type the Korean word", state.prompt)
    assertEquals("", state.input)
    assertEquals(200, state.score)
  }

  @Test fun `typing sagwa completes first word game`() = runTest(dispatcher) {
    val viewModel = HangulBuildViewModel(autoAdvanceDelayMillis = 1)
    listOf("ㄱ", "ㅏ").forEach(viewModel::onKeyClick)
    advanceTimeBy(2)
    listOf("ㄴ", "ㅏ").forEach(viewModel::onKeyClick)
    advanceTimeBy(2)

    listOf("ㅅ", "ㅏ", "ㄱ", "ㅗ", "ㅏ").forEach(viewModel::onKeyClick)

    assertEquals("사과", viewModel.uiState.value.input)
    assertEquals(300, viewModel.uiState.value.score)
    assertEquals(3, viewModel.uiState.value.combo)
    assertTrue(viewModel.uiState.value.isCorrect)
  }

  @Test fun `completing all words awards stage clear bonus`() = runTest(dispatcher) {
    val viewModel = HangulBuildViewModel(autoAdvanceDelayMillis = 1)
    val answers = listOf(
      listOf("ㄱ", "ㅏ"),
      listOf("ㄴ", "ㅏ"),
      listOf("ㅅ", "ㅏ", "ㄱ", "ㅗ", "ㅏ"),
      listOf("ㅇ", "ㅜ", "ㅇ", "ㅠ"),
      listOf("ㅋ", "ㅓ", "ㅍ", "ㅣ"),
      listOf("ㅂ", "ㅏ", "ㄴ", "ㅏ", "ㄴ", "ㅏ"),
    )

    answers.forEachIndexed { index, answer ->
      answer.forEach(viewModel::onKeyClick)
      if (index < answers.lastIndex) advanceTimeBy(2)
    }

    val state = viewModel.uiState.value
    assertEquals("바나나", state.input)
    assertEquals(950, state.score)
    assertEquals(6, state.combo)
    assertTrue(state.isCorrect)
    assertTrue(state.isStageComplete)
    assertTrue(!state.hasNextChallenge)
  }
}
