package controllers.game.stage

import models.User
import models.game.{GameError, Message, Player, State}

case object NotStarted extends Stage {
  override def receive(message: Message, user: User, state: State): Either[GameError, State] = ???

  override def currentPlayer(state: State): Option[Player] = None
}
