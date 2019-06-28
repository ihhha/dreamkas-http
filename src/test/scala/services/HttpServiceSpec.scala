package services

import akka.actor.ActorRef
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._

class HttpServiceSpec extends WordSpec
  with Matchers
  with ScalatestRouteTest
  with MockitoSugar {

  trait HttpServiceSetup {
    private val mockPrinter1 = mock[ActorRef]
    val origin = "originTest"

    val httpService = new HttpService(mockPrinter1, None, origin)
  }

}
