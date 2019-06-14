package models.dreamkas

sealed trait FontSize {
  val value: Int
}

case object Small extends FontSize {
  override val value: Int = 0
}

case object Big extends FontSize {
  override val value: Int = 1
}
