package models.game

import play.api.libs.json.{Json, OFormat}

case class Pile(direction: String, topCard: Int)

object Pile {
  implicit val pileFormat: OFormat[Pile] = Json.format[Pile]
}
