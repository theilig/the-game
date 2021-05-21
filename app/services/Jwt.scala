package services

import java.util.Date

import com.nimbusds.jose.crypto.{MACSigner, MACVerifier}
import com.nimbusds.jose.{JWSAlgorithm, JWSHeader}
import com.nimbusds.jwt.{JWTClaimsSet, SignedJWT}
import javax.inject.Inject
import models.User
import play.api.Configuration
import play.api.mvc.Request

import scala.concurrent.duration.{FiniteDuration, HOURS}
import scala.util.Try

class Jwt @Inject() (config: Configuration) {
  private val JwtSecretKey = config.get[String]("tokenSecret")
  private val tokenDuration = FiniteDuration(config.get[Int]("tokenDurationInHours"), HOURS)
  private val issuer = "https://iblgame.heilig.com"

  def createToken(user: User): String = {
    val signer = new MACSigner(JwtSecretKey)
    val claimsSet = new JWTClaimsSet.Builder().
      subject(s"${user.userId}:${user.name}").
      issuer(issuer).
      expirationTime(new Date(new Date().getTime + tokenDuration.toMillis)).
      build
    val signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet)
    signedJWT.sign(signer)
    signedJWT.serialize()
  }

  def decodePayload(token: String): Option[User] = {
    Try({
      val signedJWT = SignedJWT.parse(token)
      val verifier = new MACVerifier(JwtSecretKey)
      if (signedJWT.verify(verifier)) {
        val claimsSet = signedJWT.getJWTClaimsSet
        if (claimsSet.getIssuer == issuer && new Date().before(claimsSet.getExpirationTime)) {
          val data = claimsSet.getSubject.split(":")
          Some(User(data(0).toInt, data(1)))
        } else {
          None
        }
      } else {
        None
      }
    }).recover({
      case _: Throwable => None
    }).get
  }

  def getUser(request: Request[Any]): Option[User] = {
    val HeaderTokenRegex = """Bearer\s+(.+?)""".r

    val authHeader = request.headers.get("Authorization").getOrElse("")
    val jwtToken = authHeader match {
      case HeaderTokenRegex(token) => token
      case _ => ""
    }
    decodePayload(jwtToken)
  }
}
