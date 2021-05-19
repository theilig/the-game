package services

import controllers.game.stage.Playing
import models.game.State

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class GameSetup()(implicit ec: ExecutionContext) {
  def setupGame(state: State): Future[State] = {
    val random = new Random
    val startingPlayerIndex = random.between(0, state.players.length)
    Future(state.fillPlayerHands().copy(
      stage = new Playing(startingPlayerIndex, played = 0, needToPlay = state.rules.cardsToPlay)
    ))
  }
}
