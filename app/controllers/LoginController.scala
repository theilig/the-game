package controllers

import dao.UserDao
import javax.inject.Inject
import models.{LoginAttempt, User}
import play.api.libs.json.Json
import play.api.mvc._
import services.Jwt

import scala.concurrent.{ExecutionContext, Future}

class LoginController @Inject()(
                                 controllerComponents: ControllerComponents,
                                 userDao: UserDao,
                                 jwt: Jwt
                               )(implicit executionContext: ExecutionContext)
                               extends AbstractController(controllerComponents) {

  def processLoginAttempt: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    try {
      val loginAttempt = request.body.asJson.get.as[LoginAttempt]
      val eventualUserRow = userDao.login(loginAttempt, request.headers.get("x-forwarded-for").getOrElse("unknown"))
      eventualUserRow.map(userRow => {
        Ok(Json.toJson(models.Authentication(models.User(userRow), jwt.createToken(User(userRow)))))
      })
    } catch {
      case _ : Throwable => Future.successful(BadRequest("Invalid Json"))
    }
  }

}
