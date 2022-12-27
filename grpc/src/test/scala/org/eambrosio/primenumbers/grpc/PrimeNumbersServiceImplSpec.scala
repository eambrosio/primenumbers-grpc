package org.eambrosio.primenumbers.grpc

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import org.eambrosio.primenumbers.{PrimeNumbersReply, PrimeNumbersRequest}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import primenumbers.PrimeNumbersServiceImpl

import scala.concurrent.duration._

class PrimeNumbersServiceImplSpec extends AnyWordSpec with BeforeAndAfterAll with Matchers with ScalaFutures {

  val testKit = ActorTestKit()

  implicit val patience: PatienceConfig = PatienceConfig(scaled(5.seconds), scaled(100.millis))
  implicit val system: ActorSystem[_]   = testKit.system

  val service = new PrimeNumbersServiceImpl(system)

  override def afterAll(): Unit =
    testKit.shutdownTestKit()

  "PrimeNumbersServiceImpl" should {
    "reply to single request" in {
      val reply = service.streamPrimeNumbers(PrimeNumbersRequest(5))

      reply.runFold("")((acc, reply) => s"$acc ${reply.primeNumbers}").futureValue.trim shouldBe "2 3 5"
    }

    "return the primer numbers up to the given number" in {
      service.prime(101) should contain theSameElementsAs List(
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101
      )
    }

    "return the an empty string for number 1" in {
      service.prime(1) shouldBe empty
    }
  }

}
