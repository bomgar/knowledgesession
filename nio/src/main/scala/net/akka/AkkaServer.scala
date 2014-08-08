package net.akka

import akka.actor._
import akka.io.{Tcp, IO}
import java.net.InetSocketAddress

import net.util.Magic

class AkkaServer(port: Int) extends Actor {
  implicit val actorSystem = context.system

  override def preStart() {

    IO(Tcp) ! Tcp.Bind(self, new InetSocketAddress(port))
  }

  def receive = {
    case Tcp.Connected(remote, _) => sender ! Tcp.Register(context.actorOf(Props(new EchoConnectionHandler(sender))))
  }
}

class EchoConnectionHandler(connection: ActorRef) extends Actor  {

  context.watch(connection)

  def receive: Receive = {
    case Tcp.Received(data) =>
      val response = data.map(b => Magic.doMagic(b).asInstanceOf[Byte])
      sender ! Tcp.Write(response)
    case _: Tcp.ConnectionClosed =>
      context.unwatch(connection)
      context.stop(self)
    case Terminated(`connection`) =>
      context.stop(self)
  }
}

object TCPEchoServer extends App {

  val actorSystem = ActorSystem("echo-service-system")

  actorSystem.actorOf(Props(new AkkaServer(9999)))
}