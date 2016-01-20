package learning20151127

import java.io.DataOutputStream
import java.net.Socket

import org.apache.log4j.Logger
import py4j.GatewayServer

/**
 * Created by huang_xw on 2015/11/27.
 */
class Py4jTest {
}

private object PythonGatewayServer {
  val logger:Logger=Logger.getLogger(this.getClass)
 def main (args: Array[String]) {
   val gatewayServer=new GatewayServer(null,0)
   gatewayServer.start(true)
   val bountPort=gatewayServer.getListeningPort
   logger.info(gatewayServer.getListeningPort)
   val callbackSocket = new Socket("127.0.0.1",8110)
   val dos = new DataOutputStream(callbackSocket.getOutputStream)
   dos.writeInt(bountPort)
   dos.close()
   callbackSocket.close()
   while (System.in.read() != -1){

   }
  }
}
