package primenumbers

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.typesafe.config.ConfigFactory
import org.eambrosio.primenumbers.PrimeNumbersServiceHandler

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object GrpcServer extends App {

  val conf = ConfigFactory
    .parseString("akka.http.server.preview.enable-http2 = on")
    .withFallback(ConfigFactory.defaultApplication())

  val system = ActorSystem[Nothing](Behaviors.empty, "PrimeNumbersGrpcServer", conf)
  new PrimeNumbersServer(system).run()

}

class PrimeNumbersServer(system: ActorSystem[_]) {

  def run(): Future[Http.ServerBinding] = {
    implicit val sys: ActorSystem[_]          = system
    implicit val ec: ExecutionContextExecutor = system.executionContext

    val service: HttpRequest => Future[HttpResponse] = PrimeNumbersServiceHandler(new PrimeNumbersServiceImpl(system))

    val bound: Future[Http.ServerBinding] =
      Http(system)
        .newServerAt(interface = "127.0.0.1", port = 9000)
        .bind(service)
        .map(_.addToCoordinatedShutdown(10.seconds))

    bound.onComplete {
      case Success(binding)   =>
        val address = binding.localAddress
        println("gRPC server bound to {}:{}", address.getHostString, address.getPort)
      case Failure(exception) =>
        println("Failed to bind gRPC endpoint, terminating system", exception)
        system.terminate()
    }

    bound
  }

}
