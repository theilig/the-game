package models.game

import play.api.libs.json.{Json, OFormat}

case class Player(userId: Int, name: String, hand: List[Int], pending: Boolean)

object Player {
  implicit val playerFormat: OFormat[Player] = Json.format[Player]
}
