package models.game

import controllers.game.stage.{Playing, Stage}
import play.api.libs.json.{Json, OFormat}

case class PlayerProjection(
                             playerId: Int,
                             players: List[Player],
                             piles: List[Pile],
                             deck: Deck,
                             rules: Rules,
                             stage: Stage
                           )

object PlayerProjection {
  implicit val projectionFormat: OFormat[PlayerProjection] = Json.format[PlayerProjection]
  def apply(id: Int, state: State): PlayerProjection = {
    state.stage match {
      case _ : Playing =>
        val playerList = state.players.map {
          case p if p.userId == id => p
          case p => p.copy(hand = p.hand.map(_ => 0))
        }
        val allCards = state.players.foldLeft(state.deck.cards)((cards, p) =>
          if (p.userId == id) {
            cards
          } else {
            cards ++ p.hand.toSet
          })
        PlayerProjection(id, playerList, state.piles, Deck(allCards), state.rules, state.stage)
      case _ => PlayerProjection(id, state.players, state.piles, state.deck, state.rules, state.stage)
    }
  }
}
