package models.schema
// AUTO-GENERATED Slick data model for table Game
trait GameTable {

  self:Tables  =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}
  /** Entity class storing rows of table Game
   *  @param gameId Database column game_id SqlType(INT), AutoInc, PrimaryKey
   *  @param deckId Database column deck_id SqlType(INT)
   *  @param state Database column state SqlType(TEXT) */
  case class GameRow(gameId: Int, deckId: Int, state: String)
  /** GetResult implicit for fetching GameRow objects using plain SQL queries */
  implicit def GetResultGameRow(implicit e0: GR[Int], e1: GR[String]): GR[GameRow] = GR{
    prs => import prs._
    GameRow.tupled((<<[Int], <<[Int], <<[String]))
  }
  /** Table description of table Game. Objects of this class serve as prototypes for rows in queries. */
  class Game(_tableTag: Tag) extends profile.api.Table[GameRow](_tableTag, Some("TheGame"), "Game") {
    def * = (gameId, deckId, state) <> (GameRow.tupled, GameRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(gameId), Rep.Some(deckId), Rep.Some(state))).shaped.<>({r=>import r._; _1.map(_=> GameRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column game_id SqlType(INT), AutoInc, PrimaryKey */
    val gameId: Rep[Int] = column[Int]("game_id", O.AutoInc, O.PrimaryKey)
    /** Database column deck_id SqlType(INT) */
    val deckId: Rep[Int] = column[Int]("deck_id")
    /** Database column state SqlType(TEXT) */
    val state: Rep[String] = column[String]("state")
  }
  /** Collection-like TableQuery object for table Game */
  lazy val Game = new TableQuery(tag => new Game(tag))
}
