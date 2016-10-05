package com.actorStudio

import akka.actor.ActorSystem

object ApplicationMain extends App {
  val system = ActorSystem("ActorStudio")
  val supervisorActor = system.actorOf(SupervisorActor.props, "supervisorActor")
  supervisorActor ! SupervisorActor.Initialize
  system.awaitTermination()
}