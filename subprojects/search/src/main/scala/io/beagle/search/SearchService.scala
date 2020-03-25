package io.beagle.search

import cats.effect.{IO, Timer}
import cats.implicits._
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, Response}
import io.circe.generic.auto._
import com.sksamuel.elastic4s.circe._
import com.sksamuel.elastic4s.cats.effect.instances._
import com.sksamuel.elastic4s.requests.analysis._
import com.sksamuel.elastic4s.requests.bulk.BulkResponse
import com.sksamuel.elastic4s.requests.cluster.ClusterHealthResponse
import com.sksamuel.elastic4s.requests.delete.{DeleteByQueryResponse, DeleteResponse}
import com.sksamuel.elastic4s.requests.indexes.{CreateIndexResponse, IndexResponse}
import com.sksamuel.elastic4s.requests.mappings.{MappingDefinition, TextField}
import com.sksamuel.elastic4s.requests.searches.SearchResponse
import io.beagle.exec.Execution
import io.beagle.search.docs.FastaDoc
import org.slf4j.LoggerFactory

import scala.concurrent.duration.{FiniteDuration, _}

case class SearchService(execution: Execution, indexName: String, client: ElasticClient) {

  private val Logger = LoggerFactory.getLogger(classOf[SearchService])

  import SearchService._

  def index(fastaDoc: FastaDoc, refresh: Boolean = false): IO[Response[IndexResponse]] = {
    client.execute {
      val request = indexRequest(indexName)(fastaDoc)
      if (refresh) { request.refreshImmediately } else { request }
    }
  }

  def index(entries: List[FastaDoc]): IO[Response[BulkResponse]] = {
    val indexRequests = entries map { indexRequest(indexName) }
    client.execute { bulk(indexRequests) }
  }

  def find(sequence: String): IO[Response[SearchResponse]] = {
    client.execute { search(indexName) query sequence }
  }

  def delete(identifier: String): IO[Response[DeleteByQueryResponse]] = {
    client.execute(deleteRequest(indexName, identifier))
  }

  def deleteAll() = {
    client.execute(deleteAllRequest(indexName))
  }

  def connectionCheck(): IO[Response[ClusterHealthResponse]] = {
    def retryWithBackOff[A](ioa: IO[A], initialDelay: FiniteDuration, maxRetries: Int)(implicit timer: Timer[IO]): IO[A] = {
      ioa.handleErrorWith { error =>
        if (maxRetries > 0) {
          Logger.warn("Can not connect to search server. Retry ")
          IO.sleep(initialDelay) *> retryWithBackOff(ioa, initialDelay * 2, maxRetries - 1)
        } else
          IO.raiseError(error)
      }
    }

    implicit val timer = execution.timer
    retryWithBackOff(client.execute { clusterHealth() }, 5.seconds, 100)
  }

  def createSequenceIndex(): IO[Response[CreateIndexResponse]] = client.execute {
    createIndex(indexName).mapping(MappingDefinition().as(
      TextField(headerFieldName).analyzer(headerAnalyzer),
      TextField(sequenceFieldName).analyzer(sequenceAnalyzer)))
      .analysis(Analysis(
        analyzers = List(
          StandardAnalyzer(headerAnalyzer),
          CustomAnalyzer(sequenceAnalyzer, "ngram_ref", List.empty, List.empty)),
        tokenizers = List(NGramTokenizer("ngram_ref", 3, 4))
      ))
  }

}

object SearchService {

  val headerFieldName = "header"

  val headerAnalyzer = s"${ headerFieldName }_analyzer"

  val sequenceFieldName = "sequence"

  val sequenceAnalyzer = s"${ sequenceFieldName }_analyzer"

  private def indexRequest(indexName: String)(fastaDoc: FastaDoc) = {
    indexInto(indexName).doc(fastaDoc)
  }

  private def deleteRequest(indexName: String, identifier: String) = {
    deleteIn(indexName).by(fuzzyQuery("identifier", identifier)).refreshImmediately
  }

  private def deleteAllRequest(indexName: String) = {
    deleteIn(indexName).by(matchAllQuery()).refreshImmediately
  }

}
