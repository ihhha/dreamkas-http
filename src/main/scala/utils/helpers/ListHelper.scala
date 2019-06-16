package utils.helpers

import models.dreamkas.commands.CommandT.FS
import utils.helpers.StringHelper.StringExt

object ListHelper {

  implicit class ListStringExtended(list: List[String]) {

    def toDreamkasData: Array[Byte] = list.foldLeft(Array.emptyByteArray) {
      case (res, string) => res ++ string.toByteArray :+ FS.toByte
    }.dropRight(1)

  }

}
