package com.example.hangeultown.domain.vocabulary

data class VocabularyWord(
  val id: String,
  val korean: String,
  val english: String,
  val romanization: String,
  val emoji: String?,
  val difficulty: Int,
  val category: String,
)

object StarterVocabulary {
  val words = listOf(
    VocabularyWord("apple", "사과", "apple", "sagwa", "🍎", 1, "food"),
    VocabularyWord("milk", "우유", "milk", "uyu", "🥛", 1, "food"),
    VocabularyWord("coffee", "커피", "coffee", "keopi", "☕", 1, "cafe"),
    VocabularyWord("banana", "바나나", "banana", "banana", "🍌", 1, "food"),
  )
}
