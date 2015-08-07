package net.akka

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializerSettings, ActorMaterializer}
import akka.stream.scaladsl.Tcp.{IncomingConnection, ServerBinding}
import akka.stream.scaladsl.{Flow, Source, Tcp}
import akka.util.ByteString
import net.util.Magic

import scala.concurrent.Future

object AkkaStreamServer extends App {

  implicit val actorSystem = ActorSystem("stream-echo-service-system")

  implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(actorSystem)
      .withInputBuffer(
        initialSize = 64,
        maxSize = 64)
  )

  val connections: Source[IncomingConnection, Future[ServerBinding]] =
    Tcp().bind("127.0.0.1", 9999)

  connections runForeach { connection =>
    println(s"New connection from: ${connection.remoteAddress}")

    val magicFlow = Flow[ByteString]
      .map(_.map(b => Magic.doMagic(b.toInt).toByte))

    connection.handleWith(magicFlow)
  }

}


