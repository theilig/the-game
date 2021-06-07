package services

import controllers.game.stage.Playing
import models.game.State

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class GameSetup()(implicit ec: ExecutionContext) {
  def setupGame(state: State): Future[State] = {
    val newState = state.copy(rules = state.rules.updateForPlayerCount(state.players.length)).fillPlayerHands()
    val random = new Random
    val newPlayers = random.shuffle(newState.players)
    val startingPlayerIndex = random.between(0, newState.players.length)
    Future(newState.copy(
      players =newPlayers,
      stage = new Playing(newPlayers.drop(startingPlayerIndex).head.userId, played = 0, newState.rules.cardsToPlay)
    ))
  }
}
