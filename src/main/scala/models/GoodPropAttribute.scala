package models

object GoodPropAttribute extends Enumeration {
  type GoodPropAttribute = Value

  val ServiceDescirption: GoodPropAttribute = Value("service_description")

  val dreamkasMap: Map[GoodPropAttribute, String] = Map(ServiceDescirption -> "4")

  def toDreamkas(goodPropAttribute: GoodPropAttribute): String =
    dreamkasMap.getOrElse(goodPropAttribute, "4")
}
