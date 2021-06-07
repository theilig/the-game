package models

import play.api.libs.json.{Json, OFormat}

case class SignupInformation(name: String, email: String)

object SignupInformation {
  implicit val signupInformationFormat: OFormat[SignupInformation] = Json.format[SignupInformation]
}
