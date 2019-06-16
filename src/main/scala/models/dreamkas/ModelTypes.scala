package models.dreamkas

import models.dreamkas.errors.DreamkasError

object ModelTypes {
  type Crc = Int
  type Code = String

  type ErrorOr = Either[DreamkasError, Unit]
}
