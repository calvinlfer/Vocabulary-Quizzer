package controllers

import javax.inject.Inject

import play.api.i18n.Lang
import play.api.mvc._
import services.VocabularyService

class Quiz @Inject() (vocabularyService: VocabularyService) extends Controller {
  def quiz(sourceLanguage: Lang, targetLanguage: Lang) = Action {
    vocabularyService
      .findRandomVocabulary(sourceLanguage, targetLanguage)
      .map(v => Ok(v.word))
      .getOrElse(NotFound)
  }

  def check(sourceLanguage: Lang, word: String, targetLanguage: Lang, translation: String) = Action {
    if (vocabularyService.verify(sourceLanguage, word, targetLanguage, translation)) Ok
    else NotAcceptable
  }

  val TargetLanguageHeader = "X-Target-Language"
  def quizFromHeader(sourceLanguage: Lang) = Action { request =>
    request.headers.get(TargetLanguageHeader)
      .flatMap(targetLangStr => Lang.get(targetLangStr)) // Lang.get results in nesting so use flatMap
      .flatMap(targetLang => vocabularyService.findRandomVocabulary(sourceLanguage, targetLang)) // same reason as above
      .map(vocabulary => Ok(vocabulary.word))
      .getOrElse(NotFound)
  }
}
