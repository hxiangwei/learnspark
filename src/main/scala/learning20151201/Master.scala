package learning20151201

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging
import akka.remote.RemotingLifecycleEvent
import com.typesafe.config.ConfigFactory

import scala.collection.mutable

/**
 * Created by huang_xw on 2015/12/1.
 * huang_xw@ctrip.com
 */
class Master extends Actor {
  private val idToWorker = new mutable.HashMap[String, ApplicationInfo]
  val logger = Logging(context.system, this)

  override def preStart() = {
    logger.info("start master!")
    context.system.eventStream.subscribe(self, classOf[RemotingLifecycleEvent])
  }

  def receive = {
    case "hello" => logger.info("hello,i'm the master")
    case DeployMessage.RegisterWorker(id,
    host, port, cores, memory, webUiPort,
    publicAddress) =>
      logger.info(s"registering worker $id,$host:$port,with $cores cores,$memory memory")
      sender ! DeployMessage.RegisteredWorker("akka.tcp://sparkMaster@10.32.3.143:7079/user/master", "10.32.3.143:9091")
    case DeployMessage.Heartbeat(workerId) =>
      logger.info(s" HeartBeat from worker:$workerId")
    case _ => logger.info("unknown message!")
  }
}

object Entry extends App {
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
       |akka.remote.netty.tcp.port = 7079
       |akka.remote.netty.tcp.tcp-nodelay = on
      """.stripMargin)
  val actorSystem = ActorSystem("sparkMaster", akkaConf)
  val masterActor = actorSystem.actorOf(Props[Master], name = "master")
  println(masterActor.path)
  masterActor ! "hello"
  actorSystem.awaitTermination()
}
