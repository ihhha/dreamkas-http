package models.dreamkas

import akka.serial.SerialSettings
import models.DeviceSettingsT

case class DeviceSettings(
  override val port: String,
  override val serialSettings: SerialSettings,
  password: String
) extends DeviceSettingsT
