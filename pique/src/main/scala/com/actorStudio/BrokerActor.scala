package com.actorStudio

import akka.actor.{Actor, ActorLogging, Props}
import com.actorStudio.DocumentService._

  /**
    * Created by greg on 2016-10-03.
    * A broker is pulling news from the incoming queue and passes it to a fetcher
    */
  class BrokerActor extends Actor with ActorLogging with RandomBehaviour{

    import BrokerActor._

    lazy val fetcher = context.actorSelection("../Fetcher")
    lazy val bookkeeper = context.actorSelection("../Bookkeeper")

    def receive = {
      case FeedMe =>
        val docId = getDocumentId()
        bookkeeper ! BookkeeperActor.StartProcess(docId)
        rollTheDice()
        fetcher ! FetcherActor.FetchRequest(docId)
    }
  }


  object BrokerActor {
    val props = Props[BrokerActor]
    case object FeedMe
  }