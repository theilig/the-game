package models.game

import controllers.game.stage.{GameEnded, NotStarted, Playing, Stage}
import models.schema.Tables
import models.User
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString, JsSuccess, JsValue, Json, Reads, Writes}

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

  def updatePlayer(userId: Int)(transform: Player => Player): State = {
    copy(players = players.map {
      case p if p.userId == userId => transform(p)
      case p => p
    })
  }

  def updatePlayer(newPlayer: Player): State = {
    updatePlayer(newPlayer.userId)(_ => newPlayer)
  }

  def finishTurn: State = {
    val possiblePlayer = stage.currentPlayer(this)
    val newState = possiblePlayer.map(p => {
      val (player, newDeck) = fillPlayerHand(p, deck)
      updatePlayer(player).copy(deck = newDeck)
    }).getOrElse(this)
    newState.nextState
  }

  def nextState: State = {
    stage match {
      case Playing(currentPlayer, _, _) =>
        val nextPlayer =
          (players ::: players).dropWhile(_.userId != currentPlayer).tail.dropWhile(p => p.hand.isEmpty).headOption
        nextPlayer match {
          case None => copy(stage = GameEnded)
          case Some(player) if player.canPlay(piles) => copy(stage = Playing(player.userId, 0, rules.cardsToPlay))
          case _ => copy(stage = GameEnded)
        }
    }
  }

  def putCardOnPile(card: Int, pileIndex: Int): State = {
    copy(
      piles = piles.take(pileIndex) :::
        piles.drop(pileIndex).head.copy(topCard = card) :: piles.drop(pileIndex + 1),
      players = players.map(p => p.copy(hand = p.hand.filterNot(_ == card)))
    )
  }

  def projection(userId: Int): JsObject = {
    stage match {
      case GameEnded => JsObject(
        Seq(
          "players" -> Json.toJson(players),
          "stage" -> Json.toJson(stage)
        )
      )
      case NotStarted => JsObject(
        Seq(
          "players" -> Json.toJson(players),
          "stage" -> Json.toJson(stage)
        )
      )
      case _ =>
        val playerList = players.map {
          case p if p.userId == userId => JsObject(
            Seq(
              "hand" -> Json.toJson(p.hand),
              "name" -> JsString(p.name),
              "userId" -> JsNumber(p.userId)
            )
          )
          case p => JsObject(Seq("userId" -> JsNumber(p.userId)))
        }
        val allCards = players.foldLeft(deck.cards)((cards, p) => cards ++ p.hand.toSet).toList.sorted
        JsObject(
          Seq(
            "players" -> JsArray(playerList),
            "piles" -> Json.toJson(piles),
            "deck" -> Json.toJson(allCards),
            "rules" -> Json.toJson(rules),
            "stage" -> Json.toJson(stage)
          )
        )
    }
  }
}

object State {
  implicit val stateReads: Reads[State] = (json: JsValue) => {
    JsSuccess(State(
      (json \ "players").as[List[Player]],
      (json \ "deck").as[Deck],
      (json \ "piles").as[List[Pile]],
      (json \ "rules").as[Rules],
      (json \ "stage").as[Stage]
    ))
  }
  implicit val stateFormat: Writes[State] = (state: State) => JsObject(
    Seq(
      "players" -> Json.toJson(state.players),
      "deck" -> Json.toJson(state.deck),
      "piles" -> Json.toJson(state.piles),
      "rules" -> Json.toJson(state.rules),
      "stage" -> Json.toJson(state.stage)
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
