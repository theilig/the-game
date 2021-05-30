package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import controllers.game.stage.{FrozenForSetup, GameEnded}
import dao.GameDao
import models.User
import models.game.{ConnectToGame, GameError, GameOver, GameState, LeaveGame, Message, PlayerProjection, StartGame, State, UserMessage}
import services.GameSetup

import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Random

class GameActor(gameId: Int, gameDao: GameDao)
               (implicit ec: ExecutionContext) extends Actor with ActorLogging {
  // Placeholder state while we look up the actual state
  var state: Option[State] = None
  var watchers: Map[Int, ActorRef] = Map()
  var computerPlayers: Map[Int, ActorRef] = Map()
  override def preStart(): Unit = {
    val lookup = gameDao.findById(gameId).map {
      case Some(gameRow) => state = Some(State(gameRow))
      case None => context.stop(self)
    }
    // We don't want to take any messages until we have the latest snapshot
    // from the database
    Await.result(lookup, GameActor.LookupTimeout)
  }

  private def setUpGame(state: State): Future[State] = {
    val random = new Random
    val newPlayers = random.shuffle(state.players.filterNot(_.pending))
    val setup = new GameSetup
    setup.setupGame(state.copy(players = newPlayers))
  }

  def processUserMessage(user: User, message: Message): Unit = {
    def sendToStage(m: Message): Unit = {
      state.foreach(currentState => {
        val result = currentState.stage.receive(m, user, currentState)
        result match {
          case Right(newState) => newState.stage match {
            case GameEnded =>
              gameDao.completeGame(gameId)
              updateGameState(newState)
            case _ => updateGameState(newState)
          }
          case Left(e) => sender() ! e
        }
      })
    }

    state.foreach(currentState => {
      message match {
        case ConnectToGame(desiredGameId) if desiredGameId == gameId =>
          watchers += (user.userId -> sender())
          sender() ! GameState(PlayerProjection(user.userId, currentState))
        case LeaveGame =>
          val result = currentState.stage.receive(LeaveGame, user, currentState)
          result match {
            case Right(newState) =>
              if (newState.players.isEmpty) {
                updateGameState(newState)
                gameDao.completeGame(gameId)
                notifyWatchers(_ => GameOver)
                context.stop(self)
              } else {
                updateGameState(newState)
                watchers -= user.userId
                sender() ! GameOver
              }
            case Left(e) => sender() ! e
          }
        case StartGame =>
          // Quickly shift into frozen state, we don't use updateGameState
          // because we want to avoid notifying anyone
          state = Some(currentState.copy(stage = FrozenForSetup))
          setUpGame(currentState).map(newState => {
            computerPlayers = newState.players.filter(_.userId < 0).map(p =>
              p.userId -> context.actorOf(ComputerPlayer.props(p.userId))
            ).toMap
            updateGameState(newState)
          }).recover {
            case t: Throwable => log.error(t.getMessage)
          }
        case m => sendToStage(m)
      }
    })
  }

  override def receive: Receive = {
    case UserMessage(user, m) =>
      val permissionError = m.checkPermissionError(user, state.get)
      if (permissionError.nonEmpty) {
        sender() ! permissionError.get
      } else {
        processUserMessage(user, m)
      }
    case m => log.warning("Throwing away " + m)
  }

  private def updateGameState(newState: State): Unit = {
    gameDao.updateGame(gameId, newState).map(rowsChanged => {
      if (rowsChanged == 1) {
        state = Some(newState)
        notifyWatchers(userId => GameState(PlayerProjection(userId, state.get)))
      }
    })
  }

  def notifyWatchers(getMessage: Int => Message): Unit = {
    watchers.foreach({
      case (userId, ref) => ref ! getMessage(userId)
    })
    computerPlayers.foreach({
      case (userId, ref) => ref ! getMessage(userId)
    })
  }
}

object GameActor {
  val LookupTimeout: FiniteDuration = FiniteDuration(30, SECONDS)
  def props(gameId: Int, gameDao: GameDao)(implicit ec: ExecutionContext): Props = {
    Props(new GameActor(gameId, gameDao))
  }
}
