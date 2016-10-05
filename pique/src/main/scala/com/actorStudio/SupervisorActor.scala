package com.actorStudio

import akka.actor.{Actor, ActorLogging, Props}

class SupervisorActor extends Actor with ActorLogging {
  import SupervisorActor._

//  val broker_count = 5

  var broker = context.actorOf(BrokerActor.props, "Broker")
  var fetcher = context.actorOf(FetcherActor.props, "Fetcher")
  var indexer = context.actorOf(IndexerActor.props, "Indexer")
  var bookkeeper = context.actorOf(BookkeeperActor.props, "Bookkeeper")


  def receive = {
    case Initialize =>
      log.info("SupervisorActor starting")
      indexer ! IndexerActor.Initialize
  }
}

object SupervisorActor {
  val props = Props[SupervisorActor]
  case object Initialize
}