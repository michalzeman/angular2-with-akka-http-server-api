import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.Logging.InfoLevel
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.mz.training.common.rest.RestEndpointRoute
import com.mz.training.common.supervisors.DataSourceSupervisorActor
import com.mz.training.domains.address.AddressRestService
import com.mz.training.domains.item.ItemRestService
import com.mz.training.domains.user.UserRestService
import com.mz.training.health.HealthRoutes

import scala.annotation.tailrec
import scala.concurrent.Future

object Main extends App with HealthRoutes with RestEndpointRoute {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val settings = Settings(system)

  val logger = Logging(system, getClass)

  private val restEndPoints = List(new UserRestService, new AddressRestService, new ItemRestService)

  val dataSourceSupervisor = system.actorOf(DataSourceSupervisorActor.props, DataSourceSupervisorActor.actorName)

  val routes = logRequestResult("", InfoLevel)(buildRoute())

  val bindingFuture: Future[ServerBinding] = Http().bindAndHandleAsync(Route.asyncHandler(routes), settings.Http.interface, settings.Http.port)

  bindingFuture.failed.foreach(ex => {
    logger.error("Failed to bind to {}:{}!", settings.Http.interface, settings.Http.port, ex)
  })

  bindingFuture map { binding =>
    logger.info(s"Server started on port {}", binding.localAddress.getPort)
  } recoverWith { case _ => system.terminate() }

  override def buildRoute(): Route = {
    @tailrec
    def chainRoutes(routes: List[RestEndpointRoute], route: Route): Route = {
      routes match {
        case r :: rl => chainRoutes(rl, r.getRoute ~ route)
        case Nil => route
      }
    }

    handleExceptions(myExceptionHandler) {
      pathPrefix("api") {
        chainRoutes(restEndPoints, healthRoutes)
      }
    }
  }
}
