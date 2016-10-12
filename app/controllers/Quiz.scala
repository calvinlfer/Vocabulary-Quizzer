package controllers

import javax.inject.Inject

import actors.QuizActor
import play.api.i18n.Lang
import play.api.mvc._
import services.VocabularyService
import play.api.Play.current

class Quiz @Inject() (vocabularyService: VocabularyService) extends Controller {
  def quiz(sourceLanguage: Lang, targetLanguage: Lang) = Action {
    vocabularyService
      .findRandomVocabulary(sourceLanguage, targetLanguage)
      .map(v => Ok(v.word))
      .getOrElse(NotFound)
  }

  // acceptWithActor[String, String] indicates that the incoming and outgoing messages are Strings
  def quizWS(sourceLanguage: Lang, targetLanguage: Lang) = WebSocket.acceptWithActor[String, String] {

    request =>
      // Since we are using WebSockets, Play gives us a function that returns a function
      // the return function's parameter is a WS Client ActorRef used as a channel to communicate with the WS Client
      // The return value of the returned function must be a Props to instruct Play on how to create the Actor
      // for the WebSocket parlay to take place
      wsClientRef =>
        // We return Props to Play framework so it may create the Actor from its props
        // 1 per WS connection
        QuizActor.props(wsClientRef, sourceLanguage, targetLanguage, vocabularyService)
  }

  def check(sourceLanguage: Lang, word: String, targetLanguage: Lang, translation: String) = Action { request =>
    val isCorrect = vocabularyService.verify(sourceLanguage, word, targetLanguage, translation)

    // obtain the current scores from the Play session
    val correctScore = request.session.get("correct").map(s => s.toInt).getOrElse(0)
    val wrongScore = request.session.get("wrong").map(s => s.toInt).getOrElse(0)

    // Place updated values in the session in addition to returning Ok or NotAcceptable
    if (isCorrect) {
      Ok.withSession(
        "correct" -> (correctScore + 1).toString,
        "wrong" -> wrongScore.toString
      )
    } else {
      NotAcceptable.withSession(
        "correct" -> correctScore.toString,
        "wrong" -> (wrongScore + 1).toString
      )
    }
  }
}
