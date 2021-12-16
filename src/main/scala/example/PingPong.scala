package example
import akka.actor._

case object PingMessage
case object PongMessage
case object StartMessage
case object StopMessage

class Ping(pong: ActorRef) extends Actor {
  var counter = 0

  def receive = {
    case StartMessage =>
      println(s"Ping number ${counter}")
      pong ! PingMessage

    case PongMessage =>
      counter += 1
      println(s"Ping number ${counter}")
      if (counter > 10) {
        sender ! StopMessage
        println("Ping stop")
        context.stop(self)
      }
      else {
        sender ! PingMessage
      }
  }
}

class Pong extends Actor {
  var counter = 0
  def receive = {
    case PingMessage =>
      counter += 1
      println(s"Pong number ${counter}")
      sender ! PongMessage
    case StopMessage =>
      println("Pong stop")
      context.stop(self)
  }
}
object Main extends App {
  val system = ActorSystem("PingPongSystem")
  val pong = system.actorOf(Props[Pong], name = "pong")
  val ping = system.actorOf(Props(new Ping(pong)), name = "ping")
  ping ! StartMessage
}