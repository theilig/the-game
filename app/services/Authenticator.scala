package services

import dao.UserDao
import javax.inject.Inject
import models.User
import models.schema.Tables.UserRow
import play.api.mvc.Headers

import scala.concurrent.{ExecutionContext, Future}

class Authenticator @Inject() (jwt: Jwt, userDao: UserDao)(implicit ec: ExecutionContext) {

  def validate(possibleUser: Option[UserRow]): Option[User] = {
    possibleUser.map(row => User(row))
  }

  def authenticate(jwtToken: String): Future[Option[User]] = {
    jwt.decodePayload(jwtToken).map(user =>
      userDao.findById(user.userId)).getOrElse(Future.successful(None)).map(validate)
  }

  def authenticate(headers: Headers): Future[Option[User]] = {
    val HeaderTokenRegex = """Bearer\s+(.+?)""".r

    val authHeader = headers.get("Authorization").getOrElse("")
    val jwtToken = authHeader match {
      case HeaderTokenRegex(token) => token
      case _ => ""
    }
    authenticate(jwtToken)
  }
}
