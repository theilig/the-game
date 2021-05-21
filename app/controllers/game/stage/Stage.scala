package controllers.game.stage

import models.User
import models.game.{GameError, Message, Player, State}
import play.api.libs.json.{Format, JsError, JsObject, JsPath, JsString, JsSuccess, Reads, Writes}

abstract class Stage {
  def receive(message: Message, user: User, state: State): Either[GameError, State]
  def canAddPlayers: Boolean = false
  def currentPlayer(state: State): Option[Player]
}

object Stage {
  implicit val format: Format[Stage] = Format[Stage](
    Reads { js =>
      val stage = (JsPath \ "stage").read[String].reads(js)
      stage.fold(
        _ => JsError("stage undefined or incorrect"), {
          case "NotStarted" => JsSuccess(NotStarted)
          case "GameEnded" => JsSuccess(GameEnded)
          case "Voting" => (JsPath \ "data").read[Voting].reads(js)
          case "Playing" => (JsPath \ "data").read[Playing].reads(js)
        }
      )
    },
    Writes {
      case NotStarted => JsObject(Seq("stage" -> JsString("NotStarted")))
      case GameEnded => JsObject(Seq("stage" -> JsString("GameEnded")))
      case v: Voting => JsObject(
        Seq(
          "stage" -> JsString("Voting"),
          "data" -> Voting.format.writes(v)
        )
      )
      case p: Playing => JsObject(
        Seq(
          "stage" -> JsString("Playing"),
          "data" -> Playing.format.writes(p)
        )
      )
    }
  )
}

class PlayerStage(currentPlayer: Int) extends Stage {
  override def receive(message: Message, user: User, state: State): Either[GameError, State] = Right(state)

  override def currentPlayer(state: State): Option[Player] = state.players.find(_.userId == currentPlayer)
}
