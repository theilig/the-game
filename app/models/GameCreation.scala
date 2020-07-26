package models

import play.api.libs.json.{Json, OFormat}

case class GameCreation(visitingTeamId: Int, homeTeamId: Int, parkId: Int, geoId: Int, month: String, tod: String)

object GameCreation {
  implicit val gameCreationFormat: OFormat[GameCreation] = Json.format[GameCreation]

}
