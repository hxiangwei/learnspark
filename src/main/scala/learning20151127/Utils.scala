package learning20151127


import utils.BaseUtil

import scala.collection.JavaConversions._
import scala.collection.Map


/**
 * Created by huang_xw on 2015/11/26.
 */
private object SparkSubmitAction extends Enumeration {
  // type SparkSubmitAction = Value
  val SUBMIT, KILL, REQUEST_STATUS = Value
}

class Utils {

  import SparkSubmitAction._

  def getSystemProperties: Map[String, String] = {
    val sysProps = for (key <- System.getProperties.stringPropertyNames()) yield
    (key, System.getProperty(key))
    sysProps.toMap
  }

  def sparkOperate(operate: SparkSubmitAction.Value) = {
    operate match {
      case SUBMIT => println("submit the app")
      case KILL => println("kill process")
      case REQUEST_STATUS => println("get the process status")
      case _ => println("unknown requests")
    }
  }
}

object Utils {
  def apply() = new Utils
}

object test extends App {

  import SparkSubmitAction._

  Utils().sparkOperate(SUBMIT)
  BaseUtil.tryOrExit {
    println(123)
  }

  //val props = Utils().getSystemProperties
  //props.foreach(e => println(e._1, e._2))
}
