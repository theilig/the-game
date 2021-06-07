package dao

import models.schema.Tables
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import models.{ConfirmationToken, LoginAttempt, SignupInformation, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import services.Jwt

import java.sql.Timestamp
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class UserDao @Inject()(
                         jwt: Jwt,
                         protected val dbConfigProvider: DatabaseConfigProvider
             )(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val Users = TableQuery[Tables.User]

  private val UserConfirmations = TableQuery[Tables.UserConfirmation]

  private val insertConfirmationQuery = UserConfirmations returning UserConfirmations.map(_.userConfirmationId) into
    ((userConfirmation, userConfirmationId) => userConfirmation.copy(userConfirmationId = userConfirmationId))

  private val createUserQuery = Users returning Users.map(_.userId) into
    ((user, userId) => user.copy(userId = userId))

  def login(loginAttempt: LoginAttempt): Future[Tables.UserConfirmationRow] = {
    findByEmail(loginAttempt.email).flatMap({
      case Some(userRow) =>
        getConfirmation(userRow.userId)
      case None => Future.failed(new NoSuchElementException("User not found"))
    })
  }

  def findById(userId: Int): Future[Option[Tables.UserRow]] = {
    db.run(Users.filter(_.userId === userId).result.headOption)
  }

  def findByEmail(email: String): Future[Option[Tables.UserRow]] = {
    db.run(Users.filter(_.email === email).result.headOption)
  }

  def getConfirmation(userId: Int): Future[Tables.UserConfirmationRow] = {
    db.run(UserConfirmations.filter(_.userId === userId).result.headOption).map({
      case Some(row) if row.expiration.getTime < System.currentTimeMillis() =>
        db.run(UserConfirmations.filter(_.userConfirmationId === row.userConfirmationId).delete)
        None
      case row => row
    }).flatMap(possibleRow => possibleRow.map(Future.successful).getOrElse(createConfirmation(userId)))
  }

  def createConfirmation(userId: Int): Future[Tables.UserConfirmationRow] = {
    val OneDayInMillis = 24 * 60 * 60 * 1000
    val ConfirmationLength = 8
    val confirmation = Tables.UserConfirmationRow(
      userConfirmationId = 0,
      token = Random.alphanumeric.take(ConfirmationLength).mkString,
      userId = userId,
      expiration = new Timestamp(System.currentTimeMillis() + OneDayInMillis)
    )
    db.run(insertConfirmationQuery += confirmation)
  }

  def addUser(signupInformation: SignupInformation): Future[Tables.UserConfirmationRow] = {
    val newUser = Tables.UserRow(
      0,
      signupInformation.name,
      signupInformation.email
    )
    db.run(createUserQuery += newUser).flatMap(row => {
      getConfirmation(row.userId)
    })
  }

  def confirm(confirmationToken: ConfirmationToken): Future[Option[(Tables.UserRow, String)]] = {
    findUserForConfirmationToken(confirmationToken).flatMap {
      case Some(row) =>
        db.run(UserConfirmations.filter(_.userConfirmationId === row.userConfirmationId).delete)
        if (row.expiration.getTime >= System.currentTimeMillis()) {
          findById(row.userId).map({
            case Some(userRow) => Some(userRow, jwt.createToken(User(userRow)))
            case None => None
          })
        } else {
          Future.successful(None)
        }
      case None => Future.successful(None)
    }
  }

  private def findUserForConfirmationToken(token: ConfirmationToken): Future[Option[Tables.UserConfirmationRow]] = {
    db.run(UserConfirmations.filter(_.token === token.token).result.headOption)
  }

}
