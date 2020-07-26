package models

import play.api.libs.json.{Json, OFormat}

case class User(userId: Int, name: String)

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
}
