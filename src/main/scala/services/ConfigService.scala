package services

import scala.util.Try

import cats.instances.option._
import cats.syntax.applicative._
import cats.syntax.either._
import com.typesafe.config.ConfigFactory
import models.dreamkas.{DeviceSettings, Password}
import utils.Logging

object ConfigService extends Logging {
  private val config = ConfigFactory.load()

  def getPrinter(name: String): Option[DeviceSettings] = {
    (for {
      password <- Try(config.getString(s"devices.$name.password")).toEither
      port <- Try(config.getString(s"devices.$name.port")).toEither
      baud <- Try(config.getInt(s"devices.$name.baud")).toEither
      characterSize <- Try(config.getInt(s"devices.$name.characterSize")).toEither
      twoStopBits <- Try(config.getBoolean(s"devices.$name.twoStopBits")).toEither
      parity <- Try(config.getInt(s"devices.$name.parity")).toEither
    } yield DeviceSettings(port, baud, characterSize, twoStopBits, parity, Password(password)).pure[Option])
      .valueOr { err =>
        log.error(s"Failed to getConfig printer[$name]: ${err.getLocalizedMessage}")

        None
      }
  }

  val getSerialDebug: Boolean = config.getBoolean("serial.debug")

  val getHost: String = config.getString("http.host")
  val getPort: Int = config.getInt("http.port")
  lazy val getOrigin: String = config.getString("cors.origin")
}
