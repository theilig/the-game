package controllers.game.stage
import models.User
import models.game.{GameError, Message, Player, State}

case object FrozenForSetup extends Stage {
  override def receive(message: Message, user: User, state: State): Either[GameError, State] = Right(state)

  override def currentPlayer(state: State): Option[Player] = None
}
