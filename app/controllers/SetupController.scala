package controllers

import javax.inject.Inject
import models.GameCreation
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class SetupController @Inject()(
                               cc: ControllerComponents,
                               authenticatedAction: AuthenticatedAction
                               )
                               (implicit executionContext: ExecutionContext)
    extends AbstractController(cc) {
  def showGameList(): Action[AnyContent] = authenticatedAction.async { implicit request: UserRequest[AnyContent] =>
/*    gameDao.gameList.map(list => Ok(Json.toJson(Map("games" -> list))))*/
    Future.successful(Ok("Yo"))
  }

  def newGame(): Action[AnyContent] = authenticatedAction.async { implicit request: UserRequest[AnyContent] =>
    val gameCreation = request.body.asJson.get.as[GameCreation]
    val currentUser = request.user
    Future.successful(BadRequest("Invalid Json"))
  }
}
