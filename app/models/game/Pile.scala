package models.game

import play.api.libs.json.{Json, OFormat}

case class Pile(direction: String, topCard: Int) {
  def canPlay(card: Int): Boolean = {
    if (isReverseCard(card)) {
      true
    } else {
      direction match {
        case "Up" => card > topCard
        case "Down" => card < topCard
      }
    }
  }

  def isReverseCard(card: Int): Boolean = {
    direction match {
      case "Up" => card == topCard - 10
      case "Down" => card == topCard + 10
    }
  }
}

object Pile {
  implicit val pileFormat: OFormat[Pile] = Json.format[Pile]
}
