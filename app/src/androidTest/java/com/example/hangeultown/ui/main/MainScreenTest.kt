package com.example.hangeultown.ui.learn

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class HangulBuildScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun initialChallenge_isDisplayed() {
    composeTestRule.setContent { HangulBuildScreen(HangulBuildUiState(), {}, {}, {}) }
    composeTestRule.onNodeWithText("Build ga").assertExists()
    composeTestRule.onNodeWithText("가").assertExists()
  }

  @Test
  fun tappingJamo_buildsTargetAndShowsReward() {
    val viewModel = HangulBuildViewModel(autoAdvanceDelayMillis = 60_000)
    composeTestRule.setContent {
      HangulBuildScreen(viewModel = viewModel)
    }

    composeTestRule.onNodeWithContentDescription("Hangul key ㄱ").performClick()
    composeTestRule.onNodeWithContentDescription("Hangul key ㅏ").performClick()

    composeTestRule.onNodeWithText("✓ Perfect!").assertExists()
    composeTestRule.onNodeWithText("+100 XP").assertExists()
    composeTestRule.onNodeWithText("XP 100").assertExists()
  }

  @Test
  fun mergeAnimation_hasAccessibleDescription() {
    composeTestRule.setContent {
      HangulBuildScreen(
        state = HangulBuildUiState(input = "가", score = 100, combo = 1, isCorrect = true),
        onKeyClick = {},
        onDeleteClick = {},
        onPlayAgain = {},
      )
    }

    composeTestRule.onNodeWithContentDescription("ㄱ plus ㅏ makes 가").assertExists()
  }

  @Test
  fun wordGame_showsMeaningButHidesKoreanAnswer() {
    composeTestRule.setContent {
      HangulBuildScreen(
        state = HangulBuildUiState(
          courseTitle = "First Words",
          stageLabel = "Stage 1-2",
          target = "사과",
          romanization = "sagwa",
          prompt = "Type the Korean word",
          goal = "5 jamo",
          inputHint = "Use the Hangul keyboard",
          assemblyParts = listOf("사", "과"),
          english = "apple",
          emoji = "🍎",
          progress = 1f,
          hasNextChallenge = false,
        ),
        onKeyClick = {},
        onDeleteClick = {},
        onPlayAgain = {},
      )
    }

    composeTestRule.onNodeWithText("🍎").assertExists()
    composeTestRule.onNodeWithText("APPLE").assertExists()
    composeTestRule.onNodeWithText("/sagwa/").assertExists()
    composeTestRule.onNodeWithText("사과").assertDoesNotExist()
  }

  @Test
  fun completedWordGame_showsPronunciationButton() {
    composeTestRule.setContent {
      HangulBuildScreen(
        state = HangulBuildUiState(
          target = "사과",
          romanization = "sagwa",
          prompt = "Type the Korean word",
          goal = "5 jamo",
          inputHint = "Use the Hangul keyboard",
          assemblyParts = listOf("사", "과"),
          english = "apple",
          emoji = "🍎",
          input = "사과",
          score = 300,
          combo = 3,
          isCorrect = true,
          hasNextChallenge = false,
          isWordChallenge = true,
        ),
        onKeyClick = {},
        onDeleteClick = {},
        onPlayAgain = {},
        onSpeak = {},
        canSpeak = true,
      )
    }

    composeTestRule.onNodeWithContentDescription("Hear Korean pronunciation").assertExists()
    composeTestRule.onNodeWithText("🔊 Hear Korean").assertExists()
  }

  @Test
  fun completedStage_showsClearBonusAndReplay() {
    composeTestRule.setContent {
      HangulBuildScreen(
        state = HangulBuildUiState(
          target = "바나나",
          romanization = "banana",
          prompt = "Type the Korean word",
          goal = "6 jamo",
          inputHint = "Use the Hangul keyboard",
          assemblyParts = listOf("바", "나", "나"),
          english = "banana",
          emoji = "🍌",
          input = "바나나",
          score = 950,
          combo = 6,
          isCorrect = true,
          hasNextChallenge = false,
          isWordChallenge = true,
          isStageComplete = true,
        ),
        onKeyClick = {},
        onDeleteClick = {},
        onPlayAgain = {},
        canSpeak = true,
      )
    }

    composeTestRule.onNodeWithText("★ Stage Clear!").assertExists()
    composeTestRule.onNodeWithText("+350 XP bonus").assertExists()
    composeTestRule.onNodeWithText("Play again").assertExists()
  }
}
