package utils

import akka.event.slf4j.Logger

trait Logging {
  protected val log = Logger(getClass.toString)
}
