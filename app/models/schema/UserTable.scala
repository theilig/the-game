package models.schema
// AUTO-GENERATED Slick data model for table User
trait UserTable {

  self:Tables  =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}
  /** Entity class storing rows of table User
   *  @param userId Database column user_id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(160,true)
   *  @param email Database column email SqlType(VARCHAR), Length(256,true) */
  case class UserRow(userId: Int, name: String, email: String)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Int], e1: GR[String]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table User. Objects of this class serve as prototypes for rows in queries. */
  class User(_tableTag: Tag) extends profile.api.Table[UserRow](_tableTag, Some("TheGame"), "User") {
    def * = (userId, name, email) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(userId), Rep.Some(name), Rep.Some(email))).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(INT), AutoInc, PrimaryKey */
    val userId: Rep[Int] = column[Int]("user_id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(160,true) */
    val name: Rep[String] = column[String]("name", O.Length(160,varying=true))
    /** Database column email SqlType(VARCHAR), Length(256,true) */
    val email: Rep[String] = column[String]("email", O.Length(256,varying=true))

    /** Uniqueness Index over (email) (database name user_email) */
    val index1 = index("user_email", email, unique=true)
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))
}
