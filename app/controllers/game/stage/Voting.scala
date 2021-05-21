package controllers.game.stage

import models.User
import models.game.{GameError, Message, Player, State}
import play.api.libs.json.{Format, Json}

case class Voting(currentPlayer: Int, votes: Map[String, Int]) extends Stage {
  override def receive(message: Message, user: User, state: State): Either[GameError, State] = ???

  override def currentPlayer(state: State): Option[Player] = state.players.find(p => p.userId == currentPlayer)
}

object Voting {
  implicit val format: Format[Voting] = Json.format
}


