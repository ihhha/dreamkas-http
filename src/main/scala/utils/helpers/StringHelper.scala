package utils.helpers

import models.dreamkas.ModelTypes.Code

object StringHelper {

  val EMPTY_STRING = ""

  implicit class StringExt(string: String) {
    def leftPad(
      paddedLength: Int,
      ch: Char = '.'
    ): String = {
      var remLength = paddedLength - string.length

      val builder = StringBuilder.newBuilder

      for (a <- 0 until remLength) {
        builder.append(ch)
      }

      builder.append(string)

      builder.toString()
    }

    implicit val toCode: Code = string.asInstanceOf[Code]
  }

}
