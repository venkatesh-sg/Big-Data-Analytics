/**
  * Created by venkatesh on 02/21/17.
  */


import java.io.{ByteArrayInputStream, File}
import javax.imageio.ImageIO

import sun.misc.BASE64Decoder
import unfiltered.filter.Plan
import unfiltered.jetty.SocketPortBinding
import unfiltered.request._
import unfiltered.response.{Ok, ResponseString}


object Apiplan extends Plan{
  def intent = {
    case req@GET(Path("/get")) => {
      Ok ~> ResponseString(APITestImage.Test("Input/Test/crossing/03.jpg"))
    }

    case req@POST(Path("/get_custom")) => {
      val imageByte = (new BASE64Decoder()).decodeBuffer(Body.string(req));
      val bytes = new ByteArrayInputStream(imageByte)
      val image = ImageIO.read(bytes)
      ImageIO.write(image, "png", new File("image.png"))
    }
      Ok ~> ResponseString(APITestImage.Test("image.png"))
  }

}


object  Server extends App{
  val bindingIP = SocketPortBinding(host = "127.0.0.1", port = 8080)
  unfiltered.jetty.Server.portBinding(bindingIP).plan(Apiplan).run()

}

