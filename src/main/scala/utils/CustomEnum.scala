package utils

import play.api.libs.json._

trait CustomEnum extends Enumeration {

  implicit object CustomEnumJson extends Format[Value] {
    def writes(value: Value): JsValue = JsString(value.toString)

    def reads(jsValue: JsValue): JsResult[Value] = jsValue match {
      case JsString(str) => JsSuccess(withName(str))
      case _ => JsError("String expression expected")
    }
  }

}
