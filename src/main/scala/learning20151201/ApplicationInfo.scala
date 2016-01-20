package learning20151201

import java.util.Date

import akka.actor.ActorRef

/**
 * Created by huang_xw on 2015/12/1.
 */
class ApplicationInfo(val startTime: Long,
                      val id: String,
                      val submitDate: Date,
                      val driver: ActorRef
                       ) extends Serializable {


}
