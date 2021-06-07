package controllers

import dao.UserDao
import models.schema.Tables

import javax.inject.Inject
import models.{ConfirmationToken, LoginAttempt, SignupInformation}
import play.api.libs.json.Json
import play.api.mvc._
import services.Mailer

import scala.concurrent.{ExecutionContext, Future}

class LoginController @Inject()(
                                 userDao: UserDao,
                                 mailer: Mailer,
                                 controllerComponents: ControllerComponents
                               )(implicit executionContext: ExecutionContext)
  extends AbstractController(controllerComponents) {

  private def sendConfirmation(email: String, confirmation: Tables.UserConfirmationRow): Result = {
    if (mailer.sendConfirmation(email, confirmation.token)) {
      Ok(s"A confirmation code has been sent to $email")
    } else {
      ServiceUnavailable(s"Unexpected error sending confirmation code, please try logging in later")
    }
  }

  def processLoginAttempt: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    try {
      val loginAttempt = request.body.asJson.get.as[LoginAttempt]
      userDao.login(loginAttempt).map(c => sendConfirmation(loginAttempt.email, c)).recover(
        _ => {
          BadRequest(
            """Account was not found, please sign up"""
          )
        }
      )
    } catch {
      case _: Throwable => Future.successful(BadRequest("Invalid Json"))
    }
  }

  def processSignupAttempt: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    try {
      val signupInformation = request.body.asJson.get.as[SignupInformation]
      userDao.findByEmail(signupInformation.email).flatMap {
        case Some(_) => Future.successful(BadRequest("This email already has an account"))
        case None =>
          userDao.addUser(signupInformation).map(c => sendConfirmation(signupInformation.email, c)).recover(
            _ => BadRequest(
              """An unexpected error occurred"""
            )
          )
      }
    } catch {
      case _ : Throwable => Future.successful(BadRequest("Invalid Json"))
    }
  }

  def processLogout: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Redirect("showLogin").withNewSession
  }

  def processConfirmation: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    try {
      val token = request.body.asJson.get.as[ConfirmationToken]
      userDao.confirm(token).map({
        case Some((userRow, jwtToken)) => Ok(Json.toJson(models.Authentication(models.User(userRow), jwtToken)))
        case None => BadRequest("Invalid Token")
      })
    } catch {
      case _: Throwable => Future.successful(BadRequest("Invalid Json"))
    }
  }
}
