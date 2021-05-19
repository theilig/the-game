package controllers.game.stage

import models.User
import models.game.{GameError, Message, Player, State}

abstract class Stage {
  def receive(message: Message, user: User, state: State): Either[GameError, State]
  def canAddPlayers: Boolean = false
  def currentPlayer(state: State): Option[Player]
}


object Stage {
  def apply(input: String): Stage = {
    input match {
      case "NotStarted" => NotStarted
    }
  }
}

case class PlayerStage(currentPlayer: Player) extends Stage {
  override def receive(message: Message, user: User, state: State): Either[GameError, State] = Right(state)

  override def currentPlayer(state: State): Option[Player] = Some(currentPlayer)
}
