package com.example.hangeultown.ui.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hangeultown.domain.hangul.HangulComposer
import com.example.hangeultown.domain.vocabulary.StarterVocabulary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class BuildChallenge(
  val target: String,
  val romanization: String,
  val jamo: List<String>,
  val assemblyParts: List<String> = jamo,
  val prompt: String = "Build $romanization",
  val goal: String = jamo.joinToString(" + "),
  val inputHint: String = "Tap ${jamo.joinToString(" then ")}",
  val courseTitle: String = "Starter Course",
  val stageLabel: String = "Stage 1-1",
  val english: String? = null,
  val emoji: String? = null,
  val isWordChallenge: Boolean = false,
)

data class HangulBuildUiState(
  val courseTitle: String = "Starter Course",
  val stageLabel: String = "Stage 1-1",
  val target: String = "가",
  val romanization: String = "ga",
  val prompt: String = "Build ga",
  val goal: String = "ㄱ + ㅏ",
  val inputHint: String = "Tap ㄱ then ㅏ",
  val assemblyParts: List<String> = listOf("ㄱ", "ㅏ"),
  val english: String? = null,
  val emoji: String? = null,
  val isWordChallenge: Boolean = false,
  val isStageComplete: Boolean = false,
  val input: String = "",
  val score: Int = 0,
  val combo: Int = 0,
  val progress: Float = 1f / 3f,
  val timeRemainingSeconds: Int = 30,
  val isCorrect: Boolean = false,
  val hasNextChallenge: Boolean = true,
)

class HangulBuildViewModel(
  private val autoAdvanceDelayMillis: Long = AUTO_ADVANCE_DELAY_MILLIS,
) : ViewModel() {
  private val composer = HangulComposer()
  private val vocabularyById = StarterVocabulary.words.associateBy { it.id }
  private val challenges = listOf(
    BuildChallenge(target = "가", romanization = "ga", jamo = listOf("ㄱ", "ㅏ")),
    BuildChallenge(target = "나", romanization = "na", jamo = listOf("ㄴ", "ㅏ")),
    wordChallenge("apple", listOf("ㅅ", "ㅏ", "ㄱ", "ㅗ", "ㅏ"), listOf("사", "과")),
    wordChallenge("milk", listOf("ㅇ", "ㅜ", "ㅇ", "ㅠ"), listOf("우", "유")),
    wordChallenge("coffee", listOf("ㅋ", "ㅓ", "ㅍ", "ㅣ"), listOf("커", "피")),
    wordChallenge("banana", listOf("ㅂ", "ㅏ", "ㄴ", "ㅏ", "ㄴ", "ㅏ"), listOf("바", "나", "나")),
  )
  private var challengeIndex = 0
  private val _uiState = MutableStateFlow(HangulBuildUiState())
  val uiState = _uiState.asStateFlow()

  fun onKeyClick(jamo: String) {
    if (_uiState.value.isCorrect) return
    composer.input(jamo)
    val input = composer.text
    val correct = input == _uiState.value.target
    val stageComplete = correct && challengeIndex == challenges.lastIndex
    val earnedXp = if (correct) {
      CORRECT_XP + if (stageComplete) STAGE_CLEAR_XP else 0
    } else {
      0
    }
    _uiState.update {
      it.copy(
        input = input,
        score = it.score + earnedXp,
        combo = if (correct) it.combo + 1 else it.combo,
        isCorrect = correct,
        isStageComplete = stageComplete,
      )
    }
    if (correct && challengeIndex < challenges.lastIndex) scheduleNextChallenge()
  }

  fun onDeleteClick() {
    if (_uiState.value.isCorrect) return
    composer.delete()
    _uiState.update { it.copy(input = composer.text) }
  }

  fun reset() {
    composer.clear()
    challengeIndex = 0
    _uiState.value = stateForChallenge(challenges.first(), score = 0, combo = 0)
  }

  private fun scheduleNextChallenge() {
    viewModelScope.launch {
      delay(autoAdvanceDelayMillis)
      if (!_uiState.value.isCorrect || challengeIndex >= challenges.lastIndex) return@launch
      challengeIndex += 1
      composer.clear()
      val previous = _uiState.value
      _uiState.value = stateForChallenge(
        challenge = challenges[challengeIndex],
        score = previous.score,
        combo = previous.combo,
      )
    }
  }

  private fun stateForChallenge(
    challenge: BuildChallenge,
    score: Int,
    combo: Int,
  ) = HangulBuildUiState(
    courseTitle = challenge.courseTitle,
    stageLabel = challenge.stageLabel,
    target = challenge.target,
    romanization = challenge.romanization,
    prompt = challenge.prompt,
    goal = challenge.goal,
    inputHint = challenge.inputHint,
    assemblyParts = challenge.assemblyParts,
    english = challenge.english,
    emoji = challenge.emoji,
    isWordChallenge = challenge.isWordChallenge,
    score = score,
    combo = combo,
    progress = (challengeIndex + 1f) / challenges.size,
    hasNextChallenge = challengeIndex < challenges.lastIndex,
  )

  private fun wordChallenge(
    id: String,
    jamo: List<String>,
    assemblyParts: List<String>,
  ): BuildChallenge {
    val word = vocabularyById.getValue(id)
    return BuildChallenge(
      target = word.korean,
      romanization = word.romanization,
      jamo = jamo,
      assemblyParts = assemblyParts,
      prompt = "Type the Korean word",
      goal = "${jamo.size} jamo",
      inputHint = "Use the Hangul keyboard",
      courseTitle = "First Words",
      stageLabel = "Stage 1-2",
      english = word.english,
      emoji = word.emoji,
      isWordChallenge = true,
    )
  }

  private companion object {
    const val CORRECT_XP = 100
    const val STAGE_CLEAR_XP = 350
    const val AUTO_ADVANCE_DELAY_MILLIS = 1_400L
  }
}
