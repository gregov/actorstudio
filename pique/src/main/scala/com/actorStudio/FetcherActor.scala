package com.actorStudio

import akka.actor.{Actor, ActorLogging, Props}
import com.actorStudio.DocumentService._

/**
  * Created by greg on 2016-10-03.
  */
class FetcherActor extends Actor with ActorLogging with RandomBehaviour{

  lazy val indexer = context.actorSelection("../Indexer")
  lazy val broker = context.actorSelection("../Broker")

  import FetcherActor._

  def receive = {
    case FeedMe =>
      log.info("FeedMe received, passing to the Broker")
      broker ! BrokerActor.FeedMe
    case FetchRequest(docId) =>
      val document = fetchDocument(docId)
      rollTheDice()
      indexer ! document
  }
}

object FetcherActor{
  val props = Props[FetcherActor]
  case object FeedMe
  case class FetchRequest(doc_id:String)
}