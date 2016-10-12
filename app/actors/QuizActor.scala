package actors

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import play.api.i18n.Lang
import services.VocabularyService

/**
 * A Quiz Actor that represents the server-side party in a WebSocket interaction
 * @param wsClient an actor reference to communicate with the WS client
 * @param sourceLang is the source language constraint
 * @param targetLang is the target language constraint
 * @param vocabularyService is the service used to provide quiz functionality
 */
class QuizActor(wsClient: ActorRef,
    sourceLang: Lang,
    targetLang: Lang,
    vocabularyService: VocabularyService) extends Actor with ActorLogging {

  // keep track of the word
  private var word: String = ""

  /**
   * Obtain a word for translation under the constraints of the source and target language and store it
   * Warning: This is a side-effecting function that mutates word and communicates with the WS client
   */
  def sendWord() = {
    vocabularyService
      .findRandomVocabulary(sourceLang, targetLang)
      .fold(wsClient ! s"I don't know any words for ${sourceLang.code} and ${targetLang.code}") { vocabulary =>
        wsClient ! s"Please translate ${vocabulary.word}"
        word = vocabulary.word
      }
  }

  override def preStart(): Unit = sendWord()

  override def receive: Receive = {
    case translation: String if vocabularyService.verify(sourceLang, word, targetLang, translation) =>
      wsClient ! "Correct!"
      // Send another
      sendWord()

    case _ =>
      wsClient ! "Incorrect, please try again"
  }
}

object QuizActor {
  def props(wsClientRef: ActorRef, sourceLang: Lang, targetLang: Lang, vocabService: VocabularyService) =
    Props(classOf[QuizActor], wsClientRef, sourceLang, targetLang, vocabService)
}

