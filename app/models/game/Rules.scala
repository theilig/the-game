package models.game

import play.api.libs.json.{Json, OFormat}

case class Rules(cardsInHand: Int, cardsToPlay: Int, owner: Int) {
  def updateForPlayerCount(numPlayers: Int): Rules = {
    numPlayers match {
      case 1 => copy(cardsInHand = 8)
      case 2 => copy(cardsInHand = 7)
      case _ => copy(cardsInHand = 6)
    }
  }
}

object Rules {
  implicit val deckFormat: OFormat[Rules] = Json.format[Rules]
}
