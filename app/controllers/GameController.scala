package controllers

import controllers.game._
import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class GameController @Inject()(
                                cc: ControllerComponents,
                                gameAction: GameAction,
                              )(implicit executionContext: ExecutionContext)
  extends AbstractController(cc) {

  def setGame(resource: String = ""): Action[AnyContent] =
    gameAction.async { implicit request: GameRequest[AnyContent] =>
      Future.successful(BadRequest("Unexpected message"))
    }

  def getGame(resource: String = ""): Action[AnyContent] =
    gameAction.async { implicit request: GameRequest[AnyContent] =>
      Future.successful(BadRequest("Unexpected message"))
    }
}
