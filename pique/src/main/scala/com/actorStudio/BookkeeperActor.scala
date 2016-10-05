package com.actorStudio

import akka.actor.{Actor, ActorLogging, Props}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.mutable
import scala.concurrent.duration._
/**
  * Created by greg on 2016-10-05.
  * The bookkeeper keeps track of every id that has been de-queued
  * until it exits the system.
  *
  * For each document, after a predefined time it ensures that
  * the process has finished otherwise trigger a re-processing.
  */

class BookkeeperActor extends Actor with ActorLogging{
  var pending = mutable.Set[String]()
  lazy val fetcher = context.actorSelection("../Fetcher")

  import BookkeeperActor._
  def setTimeout(docId:String) = {
    context.system.scheduler.scheduleOnce(25.seconds, self, TimeoutProcess(docId))
  }
  def receive = {
    case StartProcess(docId) =>
      pending.add(docId)
      setTimeout(docId)
      log.info(s"started processing document $docId")
    case EndProcess(docId) =>
      pending.remove(docId)
      log.info(s"finished processing document $docId")
    case TimeoutProcess(docId) =>
      if(pending contains docId) {
        fetcher ! FetcherActor.FetchRequest(docId)
        log.info(s"Document $docId timed out, ping again the fetcher")
        setTimeout(docId)
      }

  }

}

object BookkeeperActor{
  val props = Props[BookkeeperActor]
  case class StartProcess(docId:String)
  case class EndProcess(docId:String)
  case class TimeoutProcess(docId:String)
}
