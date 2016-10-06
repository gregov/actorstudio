package com.actorStudio

import akka.actor.{ActorLogging, Props}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.persistence._

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

class BookkeeperActor extends PersistentActor with ActorLogging with RandomBehaviour{
  var pending = mutable.Set[String]() // state
  lazy val fetcher = context.actorSelection("../Fetcher")
  override def persistenceId = "bookkeeper-persistence-id"

  import BookkeeperActor._
  def setTimeout(docId:String) = {
    context.system.scheduler.scheduleOnce(25.seconds, self, TimeoutProcess(docId))
  }


  def receiveCommand: Receive = {
    case payload @ StartProcess(docId) =>
      persist(payload) {
        payload => pending.add(payload.docId)
      }
      setTimeout(docId)
      log.info(s"started processing document $docId")
      rollTheDice()
    case payload @ EndProcess(docId) =>
      persist(payload) {
        payload => pending.remove(payload.docId)
      }
      log.info(s"finished processing document $docId")
      rollTheDice()
    case TimeoutProcess(docId) =>
      if(pending contains docId) {
        fetcher ! FetcherActor.FetchRequest(docId)
        log.info(s"Document $docId timed out, ping again the fetcher")
        setTimeout(docId)
      }
      rollTheDice()
  }

  def receiveRecover: Receive = {
    case payload : StartProcess => pending.add(payload.docId)
    case payload : EndProcess => pending.remove(payload.docId)
  }

}

object BookkeeperActor{
  val props = Props[BookkeeperActor]
  case class StartProcess(docId:String)
  case class EndProcess(docId:String)
  case class TimeoutProcess(docId:String)
}
