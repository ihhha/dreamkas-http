package models.dreamkas

import models.DeviceSettingsT

case class DeviceSettings(
  override val port: String,
  baud: Int = 57600,
  characterSize: Int = 8,
  twoStopBits: Boolean = false,
  parity: Int = 0,
  password: Password
) extends DeviceSettingsT
