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

  def login(loginAttempt: LoginAttempt, ip: String): Future[Tables.UserRow] = {
    findByNameAndIp(loginAttempt.name, ip).flatMap({
      case Some(userRow) => Future.successful(userRow)
      case None =>
        val newUser = Tables.UserRow(
          0,
          loginAttempt.name,
          ip
        )
        db.run(createUserQuery += newUser)
    })
  }

  def findByNameAndIp(name: String, ip: String): Future[Option[Tables.UserRow]] = {
    db.run(Users.filter(row => {row.name === name && row.ip === ip}).result.headOption)
  }

  def findById(userId: Int): Future[Option[Tables.UserRow]] = {
    db.run(Users.filter(_.userId === userId).result.headOption)
  }

}
