package models.game

import play.api.libs.json.{Json, OFormat}

case class PilePlay(pile: Int, cards: List[Int])

object PilePlay {
  implicit val pilePlayFormat: OFormat[PilePlay] = Json.format[PilePlay]
}
