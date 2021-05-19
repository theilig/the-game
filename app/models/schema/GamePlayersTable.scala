package models.schema
// AUTO-GENERATED Slick data model for table GamePlayers
trait GamePlayersTable {

  self:Tables  =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}
  /** Entity class storing rows of table GamePlayers
   *  @param playerId Database column player_id SqlType(INT), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(INT)
   *  @param gameId Database column game_id SqlType(INT)
   *  @param seat Database column seat SqlType(INT) */
  case class GamePlayersRow(playerId: Int, userId: Int, gameId: Int, seat: Int)
  /** GetResult implicit for fetching GamePlayersRow objects using plain SQL queries */
  implicit def GetResultGamePlayersRow(implicit e0: GR[Int]): GR[GamePlayersRow] = GR{
    prs => import prs._
    GamePlayersRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table GamePlayers. Objects of this class serve as prototypes for rows in queries. */
  class GamePlayers(_tableTag: Tag) extends profile.api.Table[GamePlayersRow](_tableTag, Some("TheGame"), "GamePlayers") {
    def * = (playerId, userId, gameId, seat) <> (GamePlayersRow.tupled, GamePlayersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(playerId), Rep.Some(userId), Rep.Some(gameId), Rep.Some(seat))).shaped.<>({r=>import r._; _1.map(_=> GamePlayersRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column player_id SqlType(INT), AutoInc, PrimaryKey */
    val playerId: Rep[Int] = column[Int]("player_id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column game_id SqlType(INT) */
    val gameId: Rep[Int] = column[Int]("game_id")
    /** Database column seat SqlType(INT) */
    val seat: Rep[Int] = column[Int]("seat")

    /** Index over (gameId) (database name game_key) */
    val index1 = index("game_key", gameId)
    /** Uniqueness Index over (userId) (database name user_key) */
    val index2 = index("user_key", userId, unique=true)
  }
  /** Collection-like TableQuery object for table GamePlayers */
  lazy val GamePlayers = new TableQuery(tag => new GamePlayers(tag))
}
