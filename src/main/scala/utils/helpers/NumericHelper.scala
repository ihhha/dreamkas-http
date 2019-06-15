package utils.helpers

import utils.helpers.StringHelper._

sealed trait ByteArrayConvert {
  def toByteArray[T : Numeric](t: T): Array[Byte] = t.toString.toByteArray
}

object NumericHelper {

  implicit class IntExtended(int: Int) extends ByteArrayConvert {
    def toSymbolHex: String = int.toHexString.toUpperCase.leftPad(2, '0')

    val byteArray: Array[Byte] = toByteArray[Int](int)
  }

  implicit class LongExtended(long: Long) extends ByteArrayConvert {
    val byteArray: Array[Byte] = toByteArray[Long](long)

    val toCents: Float = long / 100F
  }

  implicit class FloatExtended(float: Float) extends ByteArrayConvert {
    val byteArray: Array[Byte] = toByteArray[Float](float)
  }

}
