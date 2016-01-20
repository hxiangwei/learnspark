package learning20151127

import java.lang.reflect._
/**
 * Created by huang_xw on 2015/11/27.
 */
class TestSpark {

}

object TestSpark extends App{
  val mainClass=Class.forName("org.apache.spark.api.python.PythonGatewayServer")
  val mainMethod=mainClass.getMethod("main",new scala.Array[String](0).getClass)
  if(!Modifier.isStatic(mainMethod.getModifiers)){
    throw new IllegalStateException("The main method in the given main class must be static")
  }
  mainMethod.invoke(null)
}
