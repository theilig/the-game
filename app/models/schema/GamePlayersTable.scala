package models.schema
// AUTO-GENERATED Slick data model for table GamePlayers
trait GamePlayersTable {

  self:Tables  =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}
  /** Entity class storing rows of table GamePlayers
   *  @param userId Database column user_id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(200,true)
   *  @param gameId Database column game_id SqlType(INT)
   *  @param seat Database column seat SqlType(INT) */
  case class GamePlayersRow(userId: Int, name: String, gameId: Int, seat: Int)
  /** GetResult implicit for fetching GamePlayersRow objects using plain SQL queries */
  implicit def GetResultGamePlayersRow(implicit e0: GR[Int], e1: GR[String]): GR[GamePlayersRow] = GR{
    prs => import prs._
    GamePlayersRow.tupled((<<[Int], <<[String], <<[Int], <<[Int]))
  }
  /** Table description of table GamePlayers. Objects of this class serve as prototypes for rows in queries. */
  class GamePlayers(_tableTag: Tag) extends profile.api.Table[GamePlayersRow](_tableTag, Some("TheGame"), "GamePlayers") {
    def * = (userId, name, gameId, seat) <> (GamePlayersRow.tupled, GamePlayersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(userId), Rep.Some(name), Rep.Some(gameId), Rep.Some(seat))).shaped.<>({r=>import r._; _1.map(_=> GamePlayersRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(INT), AutoInc, PrimaryKey */
    val userId: Rep[Int] = column[Int]("user_id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(200,true) */
    val name: Rep[String] = column[String]("name", O.Length(200,varying=true))
    /** Database column game_id SqlType(INT) */
    val gameId: Rep[Int] = column[Int]("game_id")
    /** Database column seat SqlType(INT) */
    val seat: Rep[Int] = column[Int]("seat")

    /** Index over (gameId) (database name game_id) */
    val index1 = index("game_id", gameId)
  }
  /** Collection-like TableQuery object for table GamePlayers */
  lazy val GamePlayers = new TableQuery(tag => new GamePlayers(tag))
}
