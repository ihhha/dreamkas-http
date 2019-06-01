package models

import akka.serial.SerialSettings

trait DeviceSettingsT {
  val port: String
  val serialSettings: SerialSettings
}
