package controllers.game

import controllers.UserRequest
import javax.inject.Inject
import models.User
import play.api.mvc._
import services.Jwt

import scala.concurrent.{ExecutionContext, Future}

class GameAction @Inject()(parser: BodyParsers.Default, jwt: Jwt)(implicit ec: ExecutionContext)
  extends ActionBuilder[GameRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = parser

  override protected def executionContext: ExecutionContext = ec

  override def invokeBlock[A](request: Request[A], block: GameRequest[A] => Future[Result]): Future[Result] = {
/*    try {
      val user: Option[User] = jwt.getUser(request)
      val possibleGame: Future[Option[GameRow]] = request.queryString.get("gameId").flatMap(_.headOption).map(id =>
        gameDao.findById(id.toInt)).getOrElse(Future.successful(None))
      Future.sequence(Seq(possibleUser, possibleGame)).flatMap {
        case Seq(user: Option[UserRow], game: Option[GameRow]) =>
          (UserRequest.validate(user, request), game) match {
            case (Some(userRequest), Some(gameRow)) =>
              block(controllers.game.GameRequest(State(gameRow), userRequest))
            case _ => unauthorized
          }
        case _ => unauthorized
      }
    } catch {
      case _: Throwable => unauthorized
    }*/
    Future.successful(Results.Unauthorized(""))
  }

  private def unauthorized[A] = {
    Future.successful(Results.Unauthorized(""))
  }
}
