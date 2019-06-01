package utils.helpers

object StringHelper {
  implicit class StringExt(str: String) {
    def leftPad(
      paddedLength: Int,
      ch: Char = '.'
    ): String = {
      var remLength = paddedLength - str.length

      if (remLength <= 0) str

      val builder = StringBuilder.newBuilder

      for (a <- 0 until remLength) {
        builder.append(ch)
      }

      builder.append(str)

      builder.toString()
    }
  }
}