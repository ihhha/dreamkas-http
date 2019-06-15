package models.dreamkas

import models.dreamkas.errors.DreamkasError

object ModelTypes {
  type Crc = Int
  type Code = String

  type errorOr = Either[DreamkasError, Unit]
}
