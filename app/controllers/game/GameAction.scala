package controllers.game

import controllers.UserRequest
import dao.GameDao

import javax.inject.Inject
import models.User
import models.game.State
import models.schema.Tables
import play.api.mvc._
import services.Jwt

import scala.concurrent.{ExecutionContext, Future}

class GameAction @Inject()(parser: BodyParsers.Default, jwt: Jwt, gameDao: GameDao)(implicit ec: ExecutionContext)
  extends ActionBuilder[GameRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = parser

  override protected def executionContext: ExecutionContext = ec

  override def invokeBlock[A](request: Request[A], block: GameRequest[A] => Future[Result]): Future[Result] = {
    try {
      val user: Option[User] = jwt.getUser(request)
      val possibleGame: Future[Option[Tables.GameRow]] = request.queryString.get("gameId").flatMap(_.headOption).map(id =>
        gameDao.findById(id.toInt)).getOrElse(Future.successful(None))
      possibleGame.flatMap {
        case game: Option[Tables.GameRow] =>
          user.flatMap(u => {
            game.map(g => {
              block(controllers.game.GameRequest(State(g), UserRequest(u, request)))
            })
          }).getOrElse(unauthorized)
        case _ => unauthorized
      }
    }  catch {
      case _: Throwable => unauthorized
    }
  }

  private def unauthorized[A] = {
    Future.successful(Results.Unauthorized(""))
  }
}
