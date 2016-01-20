package learning20151201

/**
 * Created by huang_xw on 2016/1/9.
 */

private sealed trait DeployMessage extends Serializable

private object DeployMessage {

  //Worker to Master
  case class RegisterWorker(id: String,
                            host: String,
                            port: Int,
                            cores: Int,
                            memory: Int,
                            webUiPort: Int,
                            publicAddress: String)

  case class RegisteredWorker(masterUrl: String, masterWebUiUrl: String) extends DeployMessage

  case object ReregisterWithMater

  case class Heartbeat(workerId:String)

  case object SendHeartBeat

}
