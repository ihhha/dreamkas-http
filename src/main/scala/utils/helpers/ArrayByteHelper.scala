package utils.helpers

import utils.helpers.NumericHelper._

object ArrayByteHelper {

  implicit class ArrayByteExtended(array: Array[Byte]) {
    def toCrc: String = array.foldLeft(0)(_ ^ _).toSymbolHex

    def toUtf8String = new String(array, "UTF-8")

  }

}
