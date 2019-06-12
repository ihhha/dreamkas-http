package services

import scala.util.Try

import akka.serial.{Parity, SerialSettings}
import cats.instances.option._
import cats.syntax.applicative._
import cats.syntax.either._
import com.typesafe.config.ConfigFactory
import models.dreamkas.{DeviceSettings, Password}

object ConfigService {
  private val config = ConfigFactory.load()

  def getPrinter(name: String): Option[DeviceSettings] = {
    (for {
      password <- Try(config.getString(s"devices.$name.password")).toEither
      port <- Try(config.getString(s"devices.$name.port")).toEither
      baud <- Try(config.getInt(s"devices.$name.baud")).toEither
      characterSize <- Try(config.getInt(s"devices.$name.characterSize")).toEither
      twoStopBits <- Try(config.getBoolean(s"devices.$name.twoStopBits")).toEither
      parity <- Try(Parity(config.getInt(s"devices.$name.parity"))).toEither
      serialSettings <- SerialSettings(baud, characterSize, twoStopBits, parity).asRight
    } yield DeviceSettings(port, serialSettings, Password(password)).pure[Option]).valueOr { err =>
      println(s"[ERROR] Failed to getConfig printer[$name]: ${err.getLocalizedMessage}")

      None
    }
  }

  val getSerialDebug: Boolean = config.getBoolean("serial.debug")

  val getHost: String = config.getString("http.host")
  val getPort: Int = config.getInt("http.port")
}
