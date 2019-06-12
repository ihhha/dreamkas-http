package utils.helpers

import models.dreamkas.ModelTypes.Code
import utils.helpers.ArrayByteHelper._

object TupleIntHelper {

  implicit class TupleIntExtended(t: (Int, Int)) {

    val toCode: Code = Array(t._1.toByte, t._2.toByte).toUtf8String

  }

}
