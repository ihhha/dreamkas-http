package models.api

import play.api.libs.functional.syntax._
import play.api.libs.json._
import models.dreamkas.commands.CommandT.FS
import utils.helpers.StringHelper.StringExt

case class Cashier(name: String, innO: Option[Int] = None) {
  val dreamkasData: Array[Byte] = {
    val innBytes = innO.map(inn => inn.toString.toByteArray :+ FS.toByte).getOrElse(Array.emptyByteArray)
    val nameCp866 = name.toCp866Bytes

    innBytes ++ nameCp866
  }

}

trait CashierNameJson {

  implicit val reads: Reads[Cashier] = (
    (__ \ "cashier_name").read[String] and
      (__ \ "inn").readNullable[Int]
    ) (Cashier.apply _)

}

object Cashier extends CashierNameJson
