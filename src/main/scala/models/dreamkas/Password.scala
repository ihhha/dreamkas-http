package models.dreamkas

case class Password(value: String = "PIRI") {
  val bytes: Array[Byte] = value.map(_.toByte).toArray
}
