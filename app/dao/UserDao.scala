package dao

import models.schema.Tables
import slick.jdbc.JdbcProfile
import javax.inject.Inject
import models.LoginAttempt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

import scala.concurrent.{ExecutionContext, Future}

class UserDao @Inject() (
               protected val dbConfigProvider: DatabaseConfigProvider
             )(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val Users = TableQuery[Tables.User]

  private val createUserQuery = Users returning Users.map(_.userId) into
    ((user, userId) => user.copy(userId = userId))

  def login(loginAttempt: LoginAttempt): Future[Tables.UserRow] = {
    val newUser = Tables.UserRow(
      0,
      loginAttempt.name
    )
    db.run(createUserQuery += newUser)
  }

}
