package primenumbers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import org.eambrosio.primenumbers.{PrimeNumbersRequest, PrimeNumbersServiceClient}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

case class PrimeNumbersEndpoint(grpcClient: PrimeNumbersServiceClient)(implicit
    ec: ExecutionContext
) extends EndpointDirectives {

  val routes: Route = heath ~ getPrimeNumbersGrpc

  def heath: Route =
    path("health") {
      get {
        complete(StatusCodes.OK, "The server is running")
      }
    }

  def getPrimeNumbersGrpc: Route =
    path("prime" / Segment) { number =>
      get {
        getPrimeNumbers(number)
      }
    }

  private def getPrimeNumbers(number: String): Route = {
    Try(number.toLong) match {
      case Failure(exception) =>
        logger.error(s"Error: ${exception.getMessage}")
        complete(StatusCodes.BadRequest)
      case Success(1)         =>
        logger.info(s"Error: 1 is not a prime number")
        complete(StatusCodes.OK, "The number 1 is not consider prime number")
      case Success(value)     =>
        complete(grpcClient.streamPrimeNumbers(PrimeNumbersRequest(value)).map(reply => reply.primeNumbers.toLong))
    }
  }

}
