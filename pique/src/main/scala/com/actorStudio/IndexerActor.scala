package com.actorStudio

import akka.actor.{Actor, ActorLogging, Props}
import com.actorStudio.DocumentService._

/**
  * Created by greg on 2016-10-04.
  */
class IndexerActor extends Actor with ActorLogging with RandomBehaviour{

  import IndexerActor._
  lazy val fetcher = context.actorSelection("../Fetcher")
  lazy val bookkeeper = context.actorSelection("../Bookkeeper")
  def receive = {
    case Initialize =>
      log.info("IndexerActor starting")
      fetcher ! FetcherActor.FeedMe
    case doc:Document =>
      // TODO: stack documents to do bulk inserts
      log.info(s"Indexer: I got ${doc.docId}")
      rollTheDice()
      indexDocument(doc)
      bookkeeper ! BookkeeperActor.EndProcess(doc.docId)
      fetcher ! FetcherActor.FeedMe
  }

    override def postRestart(reason: Throwable) {
      super.postRestart(reason)
      log.info(s"Restarted because of ${reason.getMessage}")
      self ! Initialize
    }
}


object IndexerActor {
  val props = Props[IndexerActor]
  case object Initialize
}