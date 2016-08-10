package com.mz.training.domains.user

import akka.actor.{ActorRef, Props}
import com.mz.training.common.repositories.AbstractRepositoryActor
import com.mz.training.domains.user.User

/**
  * Created by zemi on 8. 10. 2015.
  */
class UserRepositoryActor(jdbcActor: ActorRef)
  extends AbstractRepositoryActor[User](jdbcActor) with UserMapper {

}


object UserRepositoryActor {

  /**
    * Create Props for an actor of this type
    *
    * @return a Props
    */
  def props(jdbcConRef: ActorRef): Props = Props(classOf[UserRepositoryActor], jdbcConRef)
}