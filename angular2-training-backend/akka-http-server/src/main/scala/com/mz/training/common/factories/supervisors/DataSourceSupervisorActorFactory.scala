package com.mz.training.common.factories.supervisors

import akka.actor.{ActorPath, ActorRefFactory, ActorSelection}
import com.mz.training.common.supervisors.DataSourceSupervisorActor

/**
 * Created by zemi on 5. 11. 2015.
 */
trait DataSourceSupervisorActorFactory {

  val supervisorActorPath = "/user/"+DataSourceSupervisorActor.actorName

  def selectDataSourceSupervisorActor(implicit context: ActorRefFactory): ActorSelection = {
    context.actorSelection(supervisorActorPath)
  }

}
