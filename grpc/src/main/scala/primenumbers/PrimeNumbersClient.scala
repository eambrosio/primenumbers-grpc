package primenumbers

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.grpc.GrpcClientSettings
import org.eambrosio.primenumbers.{PrimeNumbersRequest, PrimeNumbersServiceClient}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object PrimeNumbersClient {

  def main(args: Array[String]): Unit = {
    implicit val sys: ActorSystem[_]  = ActorSystem(Behaviors.empty, "PrimeNumbersGrpcClient")
    implicit val ec: ExecutionContext = sys.executionContext

    val client = PrimeNumbersServiceClient(GrpcClientSettings.fromConfig("primenumbers.PrimeNumbersService")
      .withTls(false))

    val number = 20000000
    println(s"Performing request: $number")

    println(s"Streaming replies...")
    val streamResponse = client.streamPrimeNumbers(PrimeNumbersRequest(number))

    streamResponse.runForeach(reply => print(s"${reply.primeNumbers}, "))
      .onComplete {
        case Success(_) =>
          println("streamingBroadcast done")
        case Failure(e) =>
          println(s"Error streamingBroadcast: $e")
      }

  }

}
