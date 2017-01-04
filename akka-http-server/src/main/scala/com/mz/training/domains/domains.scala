package com.mz.training

import spray.json.JsonFormat

/**
 * Created by zemi on 8. 10. 2015.
 */
package object domains {

  trait EntityId {
    val id: Long
  }

}
