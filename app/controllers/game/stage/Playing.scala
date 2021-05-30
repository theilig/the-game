package controllers.game.stage
import models.User
import models.game.{FinishTurn, GameError, Message, PlayCards, Player, State}
import play.api.libs.json.{Format, Json}

case class Playing(currentPlayerId: Int, played: Int, needToPlay: Int) extends PlayerStage(currentPlayerId) {
  def playCardsOnPile(pileIndex: Int, cards: List[Int], state: State): Either[GameError, State] = {
    val initial: Either[GameError, State] = Right(state)
    cards.foldLeft(initial)((s, card) => {
      s match {
        case Right(currentState) =>
          if (currentState.piles(pileIndex).canPlay(card)) {
            Right(currentState.putCardOnPile(card, pileIndex))
          } else {
            Left(GameError(s"Cannot play $card on ${currentState.piles.drop(pileIndex).head.topCard}"))
          }
        case error => error
      }
    })
  }
  override def receive(message: Message, user: User, state: State): Either[GameError, State] = {
    message match {
      case PlayCards(plays) =>
        val initial: Either[GameError, State] = Right(state)
        val newState = plays.foldLeft(initial)((s, play) => {
          s match {
            case Right(currentState) =>
              val pile = currentState.piles.drop(play.pile).headOption
              pile match {
                case Some(_) => playCardsOnPile(play.pile, play.cards, currentState)
                case None => Left(GameError("Invalid Pile"))
              }
            case error => error
          }
        })
        newState.map(s => {
          val cardsPlayed = played + plays.foldLeft(0)((total, play) => {
            total + play.cards.length
          })
          if (cardsPlayed >= needToPlay) {
            if (s.stage.currentPlayer(s).get.hand.isEmpty || s.players.length == 1) {
              /* For one player game FinishTurn is implied with PlayCards,
               * also if the player is out of cards the turn is over
               */
              s.finishTurn
            } else {
              s.copy(stage = copy(played = cardsPlayed))
            }
          } else if (s.stage.currentPlayer(s).get.canPlay(s.piles)) {
            s.copy(stage = copy(played = cardsPlayed))
          } else {
            s.copy(stage = GameEnded)
          }
        })
      case FinishTurn if played >= needToPlay => Right(state.finishTurn)
      case _ => Left(GameError("unexpected message"))
    }
  }

  override def currentPlayer(state: State): Option[Player] = state.players.find(p => p.userId == currentPlayerId)
}

object Playing {
  implicit val format: Format[Playing] = Json.format
}
