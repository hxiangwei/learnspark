package learning20151201

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{Actor, ActorSystem, Cancellable, Props}
import akka.remote.RemotingLifecycleEvent
import com.typesafe.config.ConfigFactory
import learning20151201.DeployMessage.Heartbeat
import org.apache.spark.{Logging, SparkConf}
import utils.BaseUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 *
 * @param host host akka binding on
 * @param port worker port
 * @param webUiPort webUiPort
 * @param cores  cores of worker
 * @param memory memory of worker (MB)
 * @param masterAkkaUrls master's url
 * @param actorSystemName actorSystemName
 * @param actorName actorName
 * @param workDirPath workDir
 * @param conf sparkConf
 */
class Worker(host: String,
             port: Int,
             webUiPort: Int,
             cores: Int,
             memory: Int,
             masterAkkaUrls: Array[String],
             actorSystemName: String,
             actorName: String,
             workDirPath: String,
             val conf: SparkConf) extends Actor with Logging {
  @volatile private var registered = false
  private val workId = generateWorkerId()
  var registrationRetryTimer: Option[Cancellable] = None

  private def createDateFormat = new SimpleDateFormat("yyyyMMddHHmmss")

  private def generateWorkerId(): String = {
    "worker-%s-%s-%d".format(createDateFormat.format(new Date()), host, port)
  }

  private def tryRegisterAllMaters(): Unit = {
    for (masterAkkaUrl <- masterAkkaUrls) {
      logInfo("Connecting to Mater " + masterAkkaUrl + "...")
      val actor = context.actorSelection(masterAkkaUrl)
      actor ! DeployMessage.RegisterWorker(workId, host, port, cores, memory, webUiPort, host)
      //actor ! "hello"
    }
  }

  /**
   * TODO:Method's body
   * Re-register with the master because a network failure or a master failure has occurred.
   * If the re-registration attempt threshold is exceeded , the worker exits with error.
   * Note that for thread-safety this should only be called from the actor.
   */
  private def registerWithMaster(): Unit = {
    //    registrationRetryTimer match {
    //      case None =>
    //        registered = false
    //
    //    }
  }

  override def receive: PartialFunction[Any, Unit] = {
    case DeployMessage.RegisteredWorker(masterUrl, masterWebUrl) =>
      logInfo("Successfully registered with master " + masterUrl)
      registered = true
      context.system.scheduler.schedule(0.millis, 2000.millis, sender(), Heartbeat(workId))
    case "heartBeat" => logInfo("heartbeat response form master:")
    case _ => logInfo("can't understand")
  }

  override def preStart() {
    assert(!registered)
    logInfo("starting spark worker %s:%d with %d cores,%s RAM".format(
      host, port, cores, BaseUtil.bytesToString(memory))
    )
    logInfo("createWorkerDir()")
    context.system.eventStream.subscribe(self, classOf[RemotingLifecycleEvent])
    logInfo("start shuffle service!")
    logInfo("bind webUi")
    tryRegisterAllMaters()
    logInfo("registerSource")
  }
}

object test extends App {
  val akkaConf = ConfigFactory.parseString(
    s"""
       |akka.daemonic = on
       |akka.loggers = [""akka.event.slf4j.Slf4jLogger""]
       |akka.stdout-loglevel = "ERROR"
       |akka.jvm-exit-on-fatal-error = off
       |akka.remote.transport-failure-detector.heartbeat-interval = 3s
       |akka.remote.transport-failure-detector.acceptable-heartbeat-pause = 10s
       |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.transport-class = "akka.remote.transport.netty.NettyTransport"
       |akka.remote.netty.tcp.hostname = "10.32.3.143"
       |akka.remote.netty.tcp.port = 7080
       |akka.remote.netty.tcp.tcp-nodelay = on
      """.stripMargin)
  val actorSystem = ActorSystem("WorkerSystem", akkaConf)
  val masterActor = actorSystem.actorOf(Props(classOf[Worker],
    "10.32.3.143", 7080, 9090, 10, 20,
    Array[String]("akka.tcp://sparkMaster@10.32.3.143:7079/user/master"),
    "WorkerSystem", "worker", "/usr/local", null),
    name = "Worker")
  masterActor ! "hello"
  actorSystem.awaitTermination()
}
