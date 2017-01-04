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

object Main extends App with HealthRoutes {
  
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher 

  val settings = Settings(system)

  val logger = Logging(system, getClass)

  /** Use Guice for Dependency Injection. Remove if not required */
//  private val injector = Guice.createInjector(UserServiceModule)
//  private val userService = injector.getInstance(classOf[UserService])

  private val restEndpoinds = List(new UserRestService, new AddressRestService, new ItemRestService)

  val dataSourceSupervisor = system.actorOf(DataSourceSupervisorActor.props, DataSourceSupervisorActor.actorName)

//  val routes = logRequestResult("", InfoLevel)(userService.userRoutes ~ healthRoutes ~ addressRestService.routes ~ itemRestService.routes)
  val routes = logRequestResult("", InfoLevel)(buildRoutes())

  val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(routes, settings.Http.interface, settings.Http.port)

  bindingFuture.onFailure{ case ex: Exception =>
    //TODO: add logging
    println(ex, "Failed to bind to {}:{}!", settings.Http.interface, settings.Http.port)
  }

  bindingFuture map { binding =>
    logger.info(s"Server started on port {}", binding.localAddress.getPort)
  } recoverWith { case _ => system.terminate() }

  def buildRoutes(): Route = {
    @tailrec
    def chainRoutes(routes: List[RestEndpointRoute], route: Route): Route = {
      routes match {
        case r::rl => chainRoutes(rl, r.getRoute ~ route)
        case Nil => route
      }
    }
    chainRoutes(restEndpoinds, healthRoutes)
  }
}
