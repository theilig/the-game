package controllers

import dao.UserDao
import javax.inject.Inject
import models.LoginAttempt
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class LoginController @Inject()(
                                 controllerComponents: ControllerComponents,
                                 userDao: UserDao
                               )(implicit executionContext: ExecutionContext)
                               extends AbstractController(controllerComponents) {

  def processLoginAttempt: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    try {
      val loginAttempt = request.body.asJson.get.as[LoginAttempt]
      userDao.login(loginAttempt)
      Future.successful(Ok("User Created"))
    } catch {
      case _ : Throwable => Future.successful(BadRequest("Invalid Json"))
    }
  }

}
