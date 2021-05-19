package models.game

import controllers.game.stage.{NotStarted, Stage}
import models.schema.Tables
import models.User
import play.api.libs.json.{JsObject, JsString, JsSuccess, JsValue, Json, Reads, Writes}

import scala.annotation.tailrec

case class State(
                  players: List[Player],
                  deck: Deck,
                  piles: List[Pile],
                  rules: Rules,
                  stage: Stage
                ) {
  def fillPlayerHands(): State = {
      @tailrec
      def fillInternal(p: List[Player], d: Deck, finished: List[Player]): (List[Player], Deck) = {
        p match {
          case Nil => (finished, d)
          case x :: xs =>
            val (newPlayer, newDeck) = fillPlayerHand(x, d)
            fillInternal(xs, newDeck, newPlayer :: finished)
        }
      }
      val (newPlayers, newDeck) = fillInternal(players.reverse, deck, Nil)
      copy(players = newPlayers, deck = newDeck)
  }

  def fillPlayerHand(player: Player, deck: Deck): (Player, Deck) = {
    (player, deck) match {
      case (p, _) if p.hand.length >= rules.cardsInHand => (player, deck)
      case (_, d) if d.cards.isEmpty => (player, deck)
      case _ =>
        val (card, newDeck) = deck.deal()
        fillPlayerHand(
          player.copy(hand = player.hand.takeWhile(x => x < card) ::: (card :: player.hand.dropWhile(x => x < card))),
          newDeck
        )
    }
  }
  def projection(userId: Int): JsObject = ???
}

object State {
  implicit val stateReads: Reads[State] = (json: JsValue) => {
    JsSuccess(State(
      (json \ "players").as[List[Player]],
      (json \ "deck").as[Deck],
      (json \ "piles").as[List[Pile]],
      (json \ "rules").as[Rules],
      Stage((json \ "stage").as[String])
    ))
  }
  implicit val stateFormat: Writes[State] = (state: State) => JsObject(
    Seq(
      "players" -> Json.toJson(state.players),
      "deck" -> Json.toJson(state.deck),
      "piles" -> Json.toJson(state.piles),
      "stage" -> JsString(state.stage.getClass.getSimpleName.replace("$", ""))
    )
  )

  def apply(user: User): State = {
    new State(
      List(Player(user.userId, user.name, Nil, pending = false)),
      Deck((2 to 99).toSet),
      List(Pile("Up", 1), Pile("Up", 1), Pile("Down", 100),Pile("Down", 100)),
      Rules(cardsInHand = 8, cardsToPlay = 2, user.userId),
      NotStarted,
    )
  }

  def apply(gameRow: Tables.GameRow): State = {
    Json.parse(gameRow.state).as[State]
  }
}
