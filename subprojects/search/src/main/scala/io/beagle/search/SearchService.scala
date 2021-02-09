package io.beagle.search

import cats.effect.{IO, Resource, Sync, Timer}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.cats.effect.instances._
import com.sksamuel.elastic4s.circe._
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.requests.bulk.BulkResponse
import com.sksamuel.elastic4s.requests.cluster.ClusterHealthResponse
import com.sksamuel.elastic4s.requests.delete.DeleteByQueryResponse
import com.sksamuel.elastic4s.requests.indexes.{CreateIndexResponse, IndexResponse}
import com.sksamuel.elastic4s.requests.searches.SearchResponse
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties, Response}
import io.beagle.exec.Exec
import io.beagle.search.docs.SequenceDoc
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.generic.auto._

import scala.concurrent.duration.{FiniteDuration, _}

case class SearchService(protocol: String, host: String, port: Int, indexName: String, execution: Exec) {

  import SearchService._

  val client: Resource[IO, ElasticClient] = Resource.make {
    IO { ElasticClient(JavaClient(ElasticProperties(s"$protocol://$host:$port/"))) }
  } {
    client => IO { client.close() }
  }

  implicit def unsafeLogger[F[_] : Sync] = Slf4jLogger.getLogger[F]

  def index(fastaDoc: SequenceDoc, refresh: Boolean = false): IO[Response[IndexResponse]] = client.use {
    _.execute {
      val request = indexRequest(indexName)(fastaDoc)
      if (refresh) { request.refreshImmediately } else { request }
    }
  }

  def indexBulk(entries: List[SequenceDoc]): IO[Response[BulkResponse]] = client.use {
    _.execute { bulk(entries map { indexRequest(indexName) }) }
  }

  def find(sequence: String): IO[Response[SearchResponse]] = client.use {
    _.execute { search(indexName) query sequence }
  }

  def delete(identifier: String): IO[Response[DeleteByQueryResponse]] = client.use {
    _.execute(deleteRequest(indexName, identifier))
  }

  def deleteAll() = client.use {
    _.execute(deleteAllRequest(indexName))
  }

  def connectionCheck(): IO[Response[ClusterHealthResponse]] = {
    def retryWithBackOff[A](ioa: IO[A], initialDelay: FiniteDuration, maxRetries: Int)(implicit timer: Timer[IO]): IO[A] = {
      ioa.handleErrorWith { error =>
        if (maxRetries > 0) {
          Logger[IO].warn("Can not connect to search server. Retry ")
          IO.sleep(initialDelay) *> retryWithBackOff(ioa, initialDelay * 2, maxRetries - 1)
        } else
          IO.raiseError(error)
      }
    }

    implicit val timer = execution.timer
    client.use { client => retryWithBackOff(client.execute { clusterHealth() }, 5.seconds, 100) }
  }

  def createSequenceIndex(): IO[Response[CreateIndexResponse]] = client.use {
    _.execute {
      createIndex(indexName)
        .mapping(SequenceDoc.Mapping)
        .analysis(SequenceDoc.Analysis)
    }

  }
}

object SearchService {

  private def indexRequest(indexName: String)(fastaDoc: SequenceDoc) = {
    indexInto(indexName).doc(fastaDoc)
  }

  private def deleteRequest(indexName: String, identifier: String) = {
    deleteIn(indexName).by(fuzzyQuery("identifier", identifier)).refreshImmediately
  }

  private def deleteAllRequest(indexName: String) = {
    deleteIn(indexName).by(matchAllQuery()).refreshImmediately
  }

}
