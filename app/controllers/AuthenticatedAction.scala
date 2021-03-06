package controllers

import javax.inject.Inject
import play.api.mvc._
import services.Jwt

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedAction @Inject()(parser: BodyParsers.Default, jwt: Jwt)(implicit ec: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = parser

  override protected def executionContext: ExecutionContext = ec

  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {
    val userRequest: Option[UserRequest[A]] =
      jwt.getUser(request).map(user =>
        UserRequest(user, request)
      )
    userRequest.map(request => block(request)).getOrElse(Future.successful(Results.Unauthorized("")))
  }
}
