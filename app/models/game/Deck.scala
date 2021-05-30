package models.game

import play.api.libs.json.{Json, OFormat}

import scala.util.Random

case class Deck(cards: Set[Int]) {
  def deal(): (Int, Deck) = {
    val r = Random
    val card = cards.toVector(r.nextInt(cards.size))
    (card, Deck(cards - card))
  }
  def isEmpty: Boolean = cards.isEmpty
}

object Deck {
  implicit val deckFormat: OFormat[Deck] = Json.format[Deck]
}
