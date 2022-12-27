package primenumbers

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.Http
import com.typesafe.scalalogging.LazyLogging
import org.eambrosio.primenumbers.PrimeNumbersServiceClient

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object HttpServer extends App with LazyLogging {

  implicit val as: ActorSystem[_]   = ActorSystem(Behaviors.empty, "PrimeNumbersHttpServer")
  implicit val ec: ExecutionContext = as.executionContext

  val client = PrimeNumbersServiceClient(GrpcClientSettings.fromConfig("primenumbers.PrimeNumbersService")
    .withTls(false))

  val endpoint: PrimeNumbersEndpoint = PrimeNumbersEndpoint(client)

  val futureBinding = Http().newServerAt("0.0.0.0", 8000).bind(endpoint.routes)

  futureBinding.onComplete {
    case Success(binding) =>
      val address = binding.localAddress
      logger.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
    case Failure(ex)      =>
      logger.error("Failed to bind HTTP endpoint, terminating system", ex)
      as.terminate()
  }
}
