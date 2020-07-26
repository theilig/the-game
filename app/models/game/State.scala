package models.game

import models.User

case class State(players: List[User], stage: String)
