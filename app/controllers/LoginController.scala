package controllers

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class LoginController @Inject()(
                                 controllerComponents: ControllerComponents
                               )(implicit executionContext: ExecutionContext)
                               extends AbstractController(controllerComponents) {

  def processLoginAttempt: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    Future.successful(BadRequest("Invalid Json"))
  }

}
