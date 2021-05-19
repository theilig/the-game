package dao

import javax.inject.Inject
import models.game.State
import models.{GameListItem, User, schema}
import models.schema.Tables
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class GameDao @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                        (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  def updateGame(gameId: Int, newState: State): Future[Int] = ???

  def completeGame(gameId: Int): Future[Int] = ???

  import profile.api._

  private val Games = TableQuery[Tables.Game]

  private val Users = TableQuery[Tables.User]

  private val GamePlayers = TableQuery[Tables.GamePlayers]

  private val createGameQuery = Games returning Games.map(_.gameId) into
    ((game, gameId) => game.copy(gameId = gameId))

  def gameList: Future[List[GameListItem]] = {
    val allGamePlayers = for {
      ((game, _), player) <- (Games join GamePlayers on (_.gameId === _.gameId)) join Users on (_._2.userId === _.userId)
    } yield (game, player)
    val eventualGames = db.run(allGamePlayers.result).map( list => {
      list.groupBy(gamePair => gamePair._1)
    })

    eventualGames.map((games: Map[schema.Tables.GameRow, Seq[(schema.Tables.GameRow, schema.Tables.UserRow)]]) => {
      games.map {
        case (game, gameUserList) =>
          val state = Json.parse(game.state).as[State]
          val playerList = gameUserList.map(pair => User(pair._2)).toList
          GameListItem(game.gameId, playerList, state.stage.toString)
      }.toList
    })
  }
  def insertGame(user: User, state: State): Future[Tables.GameRow] = {
    val newGame = Tables.GameRow(0, Json.toJson(state).toString)
    db.run(createGameQuery += newGame).map(row => {
      db.run(GamePlayers += Tables.GamePlayersRow(0, user.userId, row.gameId, 1))
      row
    })
  }

  def findById(gameId: Int): Future[Option[Tables.GameRow]] = {
    db.run(Games.filter(_.gameId === gameId).result.headOption)
  }

}