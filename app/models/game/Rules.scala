package models.game

import play.api.libs.json.{Json, OFormat}

case class Rules(cardsInHand: Int, cardsToPlay: Int, owner: Int)

object Rules {
  implicit val deckFormat: OFormat[Rules] = Json.format[Rules]
}
