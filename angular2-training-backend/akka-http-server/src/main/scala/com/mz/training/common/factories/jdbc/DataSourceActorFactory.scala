package com.mz.training.common.factories.jdbc

import akka.actor._
import com.mz.training.common.factories.supervisors.DataSourceSupervisorActorFactory
import com.mz.training.common.jdbc.DataSourceActor

/**
 * Created by zemo on 05/10/15.
 */
trait DataSourceActorFactory extends DataSourceSupervisorActorFactory {

  val actorPath = supervisorActorPath+"/"+ DataSourceActor.actorName

  def selectDataSourceActor(implicit context: ActorRefFactory): ActorSelection = {
    context.actorSelection(actorPath)
  }
}
