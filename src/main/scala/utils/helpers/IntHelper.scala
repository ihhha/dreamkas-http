package utils.helpers

import utils.helpers.StringHelper._

object IntHelper {

  implicit class IntExtended(int: Int) {
    def toSymbolHex: String = int.toHexString.toUpperCase.leftPad(2, '0')
  }

}
