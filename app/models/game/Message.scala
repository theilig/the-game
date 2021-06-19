package models.game
import controllers.game.stage.PlayerStage
import models.User
import play.api.libs.json.{Format, JsError, JsObject, JsPath, JsResult, JsString, JsSuccess, Json, Reads, Writes}

sealed trait Message {
  def checkPermissionError(user: User, state: State): Option[GameError] = None
}

sealed trait OwnerMessage extends Message {
  override def checkPermissionError(user: User, state: State): Option[GameError] = {
    if (user.userId != state.rules.owner) {
      Some(GameError("You are not the game owner"))
    } else {
      None
    }
  }
}

sealed trait CurrentPlayerMessage extends Message {
  override def checkPermissionError(user: User, state: State): Option[GameError] = {
    state.stage match {
      case p : PlayerStage =>
        if (p.currentPlayer(state).exists(_.userId == user.userId)) {
          None
        } else {
          Some(GameError("It is not your turn"))
        }
      case _ => Some(GameError("Unexpected message"))
    }
  }
}

case class Authentication(token: String) extends Message

object Authentication {
  implicit val authenticationFormat: Format[Authentication] = Json.format
}

case class ConnectToGame(gameId: Int) extends Message
object ConnectToGame {
  implicit val connectFormat: Format[ConnectToGame] = Json.format
}

case class GameState(projection: PlayerProjection) extends Message
object GameState {
  implicit val gameStateFormat: Format[GameState] = Json.format
}

case class GameError(message: String) extends Message
object GameError {
  implicit val errorFormat: Format[GameError] = Json.format
}

case class AcceptPlayer(userId: Int) extends OwnerMessage
object AcceptPlayer {
  implicit val acceptPlayerFormat: Format[AcceptPlayer] = Json.format
}

case class RejectPlayer(userId: Int) extends OwnerMessage
object RejectPlayer {
  implicit val rejectPlayerFormat: Format[RejectPlayer] = Json.format
}

case class PlayCards(plays: List[PilePlay]) extends CurrentPlayerMessage

object PlayCards {
  implicit val playCardsFormat: Format[PlayCards] = Json.format
}

case object FinishTurn extends CurrentPlayerMessage

case object GameOver extends Message

case object LeaveGame extends Message

case object JoinGame extends Message

case class StartGame(cardsInHand: Int, cardsToPlay: Int) extends OwnerMessage

object StartGame {
  implicit val startGameFormat: Format[StartGame] = Json.format
}

object Message {
  implicit val messageFormat: Format[Message] = Format[Message](
    Reads { js =>
      val messageType: JsResult[String] = (JsPath \ "messageType").read[String].reads(js)
      messageType.fold(
        _ => JsError("stage undefined or incorrect"), {
          case "Authentication" => (JsPath \ "data").read[Authentication].reads(js)
          case "ConnectToGame" => (JsPath \ "data").read[ConnectToGame].reads(js)
          case "LeaveGame" => JsSuccess(LeaveGame)
          case "JoinGame" => JsSuccess(JoinGame)
          case "StartGame" => (JsPath \ "data").read[StartGame].reads(js)
          case "AcceptPlayer" => (JsPath \ "data").read[AcceptPlayer].reads(js)
          case "RejectPlayer" => (JsPath \ "data").read[RejectPlayer].reads(js)
          case "PlayCards" => (JsPath \ "data").read[PlayCards].reads(js)
          case "FinishTurn" => JsSuccess(FinishTurn)
        }
      )
    },
    Writes {
      case e: GameError =>
        JsObject(
          Seq(
            "messageType" -> JsString("Error"),
            "data" -> GameError.errorFormat.writes(e)
          )
        )
      case gs: GameState =>
        JsObject(
          Seq(
            "messageType" -> JsString("GameState"),
            "data" -> GameState.gameStateFormat.writes(gs)
          )
        )
      case m =>
        JsObject(
          Seq(
            // Scala classes end up with $ tacked at the end
            "messageType" -> JsString(m.getClass.getSimpleName.replace("$", ""))
          )
        )
    }
  )
}

