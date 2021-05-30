package actors

import akka.actor.{Actor, Props}
import controllers.game.stage.Playing
import models.game.{GameState, PilePlay, PlayCards}

import scala.concurrent.ExecutionContext

class ComputerPlayer(playerId: Int) extends Actor {
  override def receive: Receive = {
    case GameState(projection) =>
      val us = projection.players.filter(_.userId == playerId).head
      val tenRanges: List[(Int, Int)] = us.hand.foldLeft(List[(Int, Int)]())((ranges, card) => card match {
        case c if us.hand.contains(c + 10) => (c, c + 10) :: ranges
        case _ => ranges
      })
      projection.stage match {
        case Playing(id, played, needToPlay) if id == playerId =>
          sender() ! PlayCards(List(PilePlay(0, us.hand.take(needToPlay - played))))
        case _ => ;
      }
  }
}

object ComputerPlayer {
  def props(playerId: Int)(implicit executionContext: ExecutionContext): Props =
    Props(new ComputerPlayer(playerId))
}
