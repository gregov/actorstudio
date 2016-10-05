package com.actorStudio

import play.api.libs.ws.ning.NingWSClient
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by greg on 2016-10-05.
  */
case class Document(docId: String, content: String)


object DocumentService {
  val wsClient = NingWSClient()

  def getDocumentId() : String = {
    val url = "http://localhost:5000/new"
    val source = io.Source.fromURL(url)
    source.mkString
  }

  def fetchDocument(docId: String) : Document = {
    val url = s"http://localhost:5000/news/$docId"
    val source = io.Source.fromURL(url)
    val res = Document(docId, source.mkString)
    source.close()
    res
  }

  def indexDocument(doc: Document) = {
    val url = "http://localhost:5000/submit"
    wsClient
      .url(url)
      .post(Map("doc_id" -> Seq(s"${doc.docId}")))
      .map { wsResponse =>
        if (!(200 to 299).contains(wsResponse.status)) {
          throw new Exception(s"Received unexpected status ${wsResponse.status} : ${wsResponse.body}")
        }
      }
  }
}
