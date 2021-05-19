package controllers

import dao.GameDao
import javax.inject.Inject
import models.User
import models.game.State
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext

class SetupController @Inject()(
                               cc: ControllerComponents,
                               authenticatedAction: AuthenticatedAction,
                               gameDao: GameDao
                               )
                               (implicit executionContext: ExecutionContext)
    extends AbstractController(cc) {
  def showGameList(): Action[AnyContent] = authenticatedAction.async { implicit request: UserRequest[AnyContent] =>
    gameDao.gameList.map(list => Ok(Json.toJson(Map("games" -> list))))
  }

  def newGame(): Action[AnyContent] = authenticatedAction.async { implicit request: UserRequest[AnyContent] =>
    val currentUser = request.user
    val eventualGame = gameDao.insertGame(User(currentUser.userId, currentUser.name), State(currentUser))
    eventualGame.map(game => Ok(Json.toJson(Map("gameId" -> game.gameId))))
  }
}
