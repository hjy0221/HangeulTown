package com.example.hangeultown.ui.learn

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hangeultown.theme.HangeulTownTheme
import com.example.hangeultown.ui.audio.rememberKoreanSpeaker
import com.example.hangeultown.ui.components.HangulKeyboard
import kotlinx.coroutines.delay

@Composable
fun HangulBuildScreen(
  modifier: Modifier = Modifier,
  viewModel: HangulBuildViewModel = viewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val haptics = LocalHapticFeedback.current
  val speaker = rememberKoreanSpeaker()
  LaunchedEffect(state.isCorrect, state.isWordChallenge, state.target, speaker.isReady) {
    if (state.isCorrect && state.isWordChallenge && speaker.isReady) {
      speaker.speak(state.target)
    }
  }
  HangulBuildScreen(
    state = state,
    onKeyClick = {
      viewModel.onKeyClick(it)
      if (viewModel.uiState.value.isCorrect) {
        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
      }
    },
    onDeleteClick = viewModel::onDeleteClick,
    onPlayAgain = viewModel::reset,
    onSpeak = { speaker.speak(state.target) },
    canSpeak = speaker.isReady,
    modifier = modifier,
  )
}

@Composable
internal fun HangulBuildScreen(
  state: HangulBuildUiState,
  onKeyClick: (String) -> Unit,
  onDeleteClick: () -> Unit,
  onPlayAgain: () -> Unit,
  modifier: Modifier = Modifier,
  onSpeak: () -> Unit = {},
  canSpeak: Boolean = false,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .safeDrawingPadding()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column {
        Text("HANGEUL TOWN", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text(state.courseTitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
      }
      Text("XP ${state.score}", fontWeight = FontWeight.Bold)
    }
    Spacer(Modifier.height(14.dp))
    LinearProgressIndicator(
      progress = { state.progress },
      modifier = Modifier.fillMaxWidth(),
    ) {
    }
    Spacer(Modifier.height(18.dp))
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.primaryContainer,
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(state.stageLabel, fontWeight = FontWeight.Bold)
          Text("${state.timeRemainingSeconds}s", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text(
          state.prompt,
          modifier = Modifier.semantics { heading() },
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
        )
        Text("/${state.romanization}/", color = MaterialTheme.colorScheme.onPrimaryContainer)
        Spacer(Modifier.height(8.dp))
        if (state.emoji != null && state.english != null) {
          Text(state.emoji, fontSize = 64.sp, lineHeight = 72.sp)
          Text(
            state.english.uppercase(),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
          )
        } else {
          Text(state.target, fontSize = 92.sp, lineHeight = 100.sp, fontWeight = FontWeight.Black)
        }
      }
    }
    Spacer(Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      StatPill(label = "Combo", value = "${state.combo}", modifier = Modifier.weight(1f))
      StatPill(label = "Goal", value = state.goal, modifier = Modifier.weight(1f))
    }
    Spacer(Modifier.height(12.dp))
    HangulAssemblyPanel(
      input = state.input,
      target = state.target,
      inputHint = state.inputHint,
      assemblyParts = state.assemblyParts,
      isCorrect = state.isCorrect,
    )
    AnimatedVisibility(visible = state.isCorrect, enter = fadeIn() + scaleIn()) {
      Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 12.dp)) {
        Text("✓ Perfect!", color = MaterialTheme.colorScheme.primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("+100 XP", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        if (state.isStageComplete) {
          Text(
            "★ Stage Clear!",
            modifier = Modifier
              .padding(top = 8.dp)
              .semantics { heading() },
            color = MaterialTheme.colorScheme.primary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
          )
          Text("+350 XP bonus", fontWeight = FontWeight.Bold)
        }
        if (state.isWordChallenge) {
          Spacer(Modifier.height(8.dp))
          OutlinedButton(
            onClick = onSpeak,
            enabled = canSpeak,
            modifier = Modifier.semantics {
              contentDescription = "Hear Korean pronunciation"
            },
          ) {
            Text("🔊 Hear Korean")
          }
        }
      }
    }
    Spacer(Modifier.weight(1f))
    if (state.isCorrect && !state.hasNextChallenge) {
      Button(onClick = onPlayAgain, modifier = Modifier.fillMaxWidth().height(54.dp)) {
        Text(if (state.isStageComplete) "Play again" else "Replay stage")
      }
    } else if (!state.isCorrect) {
      HangulKeyboard(
        onKeyClick = onKeyClick,
        onDeleteClick = onDeleteClick,
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}

@Composable
private fun HangulAssemblyPanel(
  input: String,
  target: String,
  inputHint: String,
  assemblyParts: List<String>,
  isCorrect: Boolean,
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .height(84.dp),
    shape = RoundedCornerShape(18.dp),
    color = if (isCorrect) {
      MaterialTheme.colorScheme.tertiaryContainer
    } else {
      MaterialTheme.colorScheme.surfaceContainer
    },
  ) {
    if (isCorrect) {
      JamoMergeAnimation(
        jamo = assemblyParts,
        target = target,
      )
    } else {
      AnimatedContent(
        targetState = input.ifEmpty { inputHint },
        label = "hangulInput",
      ) { visibleInput ->
        Box(contentAlignment = Alignment.Center) {
          Text(
            visibleInput,
            textAlign = TextAlign.Center,
            fontSize = if (input.isEmpty()) 22.sp else 34.sp,
            fontWeight = FontWeight.Bold,
          )
        }
      }
    }
  }
}

@Composable
private fun JamoMergeAnimation(
  jamo: List<String>,
  target: String,
) {
  var assembled by remember(target) { mutableStateOf(false) }
  LaunchedEffect(target) {
    delay(80)
    assembled = true
  }

  val jamoAlpha by animateFloatAsState(
    targetValue = if (assembled) 0f else 1f,
    animationSpec = tween(durationMillis = 140, delayMillis = 330),
    label = "jamoFade",
  )
  val syllableAlpha by animateFloatAsState(
    targetValue = if (assembled) 1f else 0f,
    animationSpec = tween(durationMillis = 180, delayMillis = 350),
    label = "syllableFade",
  )
  val syllableScale by animateFloatAsState(
    targetValue = if (assembled) 1f else 0.6f,
    animationSpec = tween(durationMillis = 280, delayMillis = 330, easing = FastOutSlowInEasing),
    label = "syllableScale",
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .semantics {
        contentDescription = "${jamo.joinToString(" plus ")} makes $target"
      },
    contentAlignment = Alignment.Center,
  ) {
    jamo.forEachIndexed { index, letter ->
      val spread = when (jamo.size) {
        1 -> 0.dp
        2 -> if (index == 0) (-52).dp else 52.dp
        else -> ((index - (jamo.lastIndex / 2f)) * 60).dp
      }
      val offset by animateDpAsState(
        targetValue = if (assembled) 0.dp else spread,
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "jamoOffset$index",
      )
      Text(
        text = letter,
        modifier = Modifier
          .padding(horizontal = 4.dp)
          .alpha(jamoAlpha)
          .offset(x = offset),
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        fontSize = 36.sp,
        fontWeight = FontWeight.Black,
      )
    }
    Text(
      text = target,
      modifier = Modifier
        .alpha(syllableAlpha)
        .scale(syllableScale),
      color = MaterialTheme.colorScheme.onTertiaryContainer,
      fontSize = 40.sp,
      fontWeight = FontWeight.Black,
    )
  }
}

@Composable
private fun StatPill(label: String, value: String, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.secondaryContainer,
  ) {
    Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 12.sp)
        Spacer(Modifier.width(8.dp))
        Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
      }
    }
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun HangulBuildPreview() {
  HangeulTownTheme(dynamicColor = false) {
    HangulBuildScreen(HangulBuildUiState(), {}, {}, {})
  }
}
