package services

import com.sendgrid.{Content, Email, Mail, Method, Request, SendGrid}
import play.api.Configuration

import javax.inject.Inject

import java.io.IOException

class Mailer @Inject() (config: Configuration) {
  def sendConfirmation(email: String, token: String): Boolean = {
    val key = config.get[String]("sendGridApiKey")
    val sendgrid = new SendGrid(key)
    val request = new Request()

    val mail = new Mail(
      new Email("heilig@heilig.com"),
      "Sign in attempt for TheGame",
      new Email(email),
      new Content(
        "text/plain",
        s"Either enter $token into login form, or follow the link below to sign in\nhttps://games.heilig.com/confirm/$token"
      )
    )
    try {
      request.setMethod(Method.POST)
      request.setEndpoint("mail/send")
      request.setBody(mail.build())
      sendgrid.api(request)
      true
    } catch {
//      case _: IOException => false
      case e => println(e.getMessage); false
    }
  }
}
