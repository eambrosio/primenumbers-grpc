package primenumbers

import akka.http.scaladsl.server.Directives
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.AutoDerivation

trait EndpointDirectives extends LazyLogging with FailFastCirceSupport with AutoDerivation with Directives {}
