import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.Logging.InfoLevel
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.mz.training.common.rest.CorsSupport
import com.mz.training.common.supervisors.DataSourceSupervisorActor
import com.mz.training.domains.address.AddressRestService
import com.mz.training.domains.item.ItemRestService
import com.mz.training.domains.user.UserRestService
import com.mz.training.health.HealthRoutes

object Main extends App with HealthRoutes {
  
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher 

  val settings = Settings(system)

  val logger = Logging(system, getClass)

  /** Use Guice for Dependency Injection. Remove if not required */
//  private val injector = Guice.createInjector(UserServiceModule)
//  private val userService = injector.getInstance(classOf[UserService])
  private val userService = new UserRestService(system);
  private val addressRestService = new AddressRestService(system)
  private val itemRestService = new ItemRestService(system)

  val dataSourceSupervisor = system.actorOf(DataSourceSupervisorActor.props, DataSourceSupervisorActor.actorName)

  val routes = logRequestResult("", InfoLevel)(userService.userRoutes ~ healthRoutes ~ addressRestService.routes ~ itemRestService.routes)

  Http().bindAndHandle(routes, settings.Http.interface, settings.Http.port) map { binding =>
    logger.info(s"Server started on port {}", binding.localAddress.getPort)
  } recoverWith { case _ => system.terminate() }

}
