package dao

import javax.inject.Inject
import models.game.State
import models.{GameListItem, User}
import models.schema.Tables
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class GameDao @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                        (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val Games = TableQuery[Tables.Game]

  private val createGameQuery = Games returning Games.map(_.gameId) into
    ((game, gameId) => game.copy(gameId = gameId))

  def gameList: Future[List[GameListItem]] = {
    db.run(Games.filter(_.finished === false).result).map(gameRows =>
      gameRows.map(gameRow => {
        val state = State(gameRow)
        GameListItem(gameRow)
      }).toList
    )
  }
  def insertGame(user: User, state: State): Future[Tables.GameRow] = {
    val newGame = Tables.GameRow(0, Json.toJson(state).toString, finished = false)
    db.run(createGameQuery += newGame)
  }

  def findById(gameId: Int): Future[Option[Tables.GameRow]] = {
    db.run(Games.filter(_.gameId === gameId).result.headOption)
  }

  def completeGame(gameId: Int): Future[Int] = {
    val updateQuery = for { g <- Games if g.gameId === gameId } yield g.finished
    db.run(updateQuery.update(true))
  }

  def updateGame(gameId: Int, newState: State): Future[Int] = {
    val updateQuery = for { g <- Games if g.gameId === gameId } yield g.state
    db.run(updateQuery.update(Json.toJson(newState).toString))
  }

}