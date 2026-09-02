package com.example.hangeultown.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val KEY_ROWS = listOf(
  listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ"),
  listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ"),
  listOf("ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ"),
)

@Composable
fun HangulKeyboard(
  onKeyClick: (String) -> Unit,
  onDeleteClick: () -> Unit,
  enabled: Boolean = true,
  modifier: Modifier = Modifier,
) {
  val haptics = LocalHapticFeedback.current
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
    KEY_ROWS.forEach { keys ->
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        keys.forEach { key ->
          KeyboardKey(
            label = key,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            onClick = {
              haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onKeyClick(key)
            },
          )
        }
      }
    }
    Button(
      onClick = {
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        onDeleteClick()
      },
      enabled = enabled,
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 52.dp)
        .semantics { contentDescription = "Delete last jamo" },
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
      ),
    ) { Text("⌫  Delete", fontSize = 16.sp) }
  }
}

@Composable
private fun KeyboardKey(
  label: String,
  enabled: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val pressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(if (pressed) 0.92f else 1f, label = "keyPress")
  Button(
    onClick = onClick,
    enabled = enabled,
    interactionSource = interactionSource,
    modifier = modifier
      .scale(scale)
      .heightIn(min = 52.dp)
      .semantics { contentDescription = "Hangul key $label" },
    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
    shape = RoundedCornerShape(14.dp),
  ) { Text(label, fontSize = 20.sp) }
}
