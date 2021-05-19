package models

import play.api.libs.json.{Json, OFormat}
import models.schema.Tables
case class User(userId: Int, name: String)

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
  def apply(row: Tables.UserRow): User = {
    new User(row.userId, row.name)
  }
}
