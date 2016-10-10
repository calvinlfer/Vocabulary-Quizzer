package services

import javax.inject.Singleton

import models.Vocabulary
import play.api.i18n.Lang

import scala.util.Random

/**
 * Specifies that the VocabularyService class has singleton scope, which means the same instance will be
 * injected in all classes having a dependency on it
 */
@Singleton
class VocabularyService {

  // Minimal set of words to begin
  private var allVocabulary = List(
    Vocabulary(Lang("en"), Lang("fr"), "hello", "bonjour"),
    Vocabulary(Lang("en"), Lang("fr"), "play", "jouer")
  )

  // add vocabulary that does not exist already
  def addVocabulary(v: Vocabulary): Boolean =
    if (!allVocabulary.contains(v)) {
      allVocabulary = v :: allVocabulary
      true
    } else {
      false
    }

  def findRandomVocabulary(sourceLanguage: Lang, targetLanguage: Lang): Option[Vocabulary] =
    // Shuffle the subset of words that matches the criteria of the source and target languages
    Random.shuffle(
      allVocabulary
        .filter(v => v.sourceLanguage == sourceLanguage)
        .filter(v => v.targetLanguage == targetLanguage)
    ).headOption

  def verify(sourceLanguage: Lang, word: String, targetLanguage: Lang, translation: String): Boolean =
    // verifies if a proposed translation is correct by looking for a Vocabulary that matches
    allVocabulary.contains(Vocabulary(sourceLanguage, targetLanguage, word, translation))
}
