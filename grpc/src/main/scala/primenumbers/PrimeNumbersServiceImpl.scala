package primenumbers

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.Source
import org.eambrosio.primenumbers.{PrimeNumbersReply, PrimeNumbersRequest, PrimeNumbersService}

import scala.collection.compat.immutable.LazyList
import scala.math.sqrt
import scala.util.Try

class PrimeNumbersServiceImpl(system: ActorSystem[_]) extends PrimeNumbersService {
  private implicit val sys: ActorSystem[_] = system

  /**
   * Ask for prime numbers up to the given number
   */
  override def streamPrimeNumbers(in: PrimeNumbersRequest): Source[PrimeNumbersReply, NotUsed] =
    Source.fromIterator(() =>
      prime(in.number)
        .iterator
        .map(n => PrimeNumbersReply(n.toString))
    )

  def prime(n: Long) =
    Try(primes.takeWhile(_ <= n)).toOption.getOrElse(LazyList())

  private val primes: LazyList[Int] = LazyList.from(2)
    .filter(isPrime)

  private def isPrime(n: Int) =
    Range.inclusive(2, sqrt(n).toInt).forall(n % _ != 0)

}
