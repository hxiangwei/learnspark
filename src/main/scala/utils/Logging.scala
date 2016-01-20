package utils

import org.slf4j.{LoggerFactory, Logger}

/**
 * Created by huang_xw on 2015/12/1.
 * huang_xw@ctrip.com
 */
trait Logging {
  var initialized=false
  val initlock = new  Object()
  @transient private var log_ : Logger=null
  protected  def logName={
    this.getClass.getName.stripSuffix("$")
  }

  protected def log:Logger={
    if(log == null) {
      log_ = LoggerFactory.getLogger(logName)
    }
    log_
  }

  //param is a code block returns a string msg.
  protected def logInfo(msg: => String)={
    log.info(msg)
  }

  protected  def logDebug(msg: => String)={
    log.debug(msg)
  }

  protected def logTrace(msg: => String)={
    log.trace(msg)
  }

  protected  def logWarning(msg: => String)={
    log.warn(msg)
  }

  protected def logError(msg: => String)={
    log.error(msg)
  }

  private  def initializeIfNecessary(): Unit ={
    if(!initialized){

    }
  }

  private def initializeLogging: Unit ={

  }
}
