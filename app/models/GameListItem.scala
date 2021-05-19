package models

import play.api.libs.json.{Json, OFormat}

case class GameListItem(id: Int, players: List[User], stage: String)

object GameListItem {
  implicit val gameListItemFormat: OFormat[GameListItem] = Json.format[GameListItem]

}