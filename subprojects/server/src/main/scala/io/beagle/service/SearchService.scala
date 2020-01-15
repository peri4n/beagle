package io.beagle.service

import cats.effect.{IO, Timer}
import cats.implicits._
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, Response}
import com.sksamuel.elastic4s.cats.effect.instances._
import com.sksamuel.elastic4s.requests.analysis.{Analysis, CustomAnalyzer, NGramTokenizer, StandardAnalyzer}
import com.sksamuel.elastic4s.requests.bulk.BulkResponse
import com.sksamuel.elastic4s.requests.cluster.ClusterHealthResponse
import com.sksamuel.elastic4s.requests.indexes.{CreateIndexResponse, IndexResponse}
import com.sksamuel.elastic4s.requests.mappings.{MappingDefinition, TextField}
import com.sksamuel.elastic4s.requests.searches.SearchResponse
import io.beagle.Env
import io.beagle.components.Execution
import io.beagle.components.settings.SearchSettings
import io.beagle.fasta.FastaEntry
import org.slf4j.LoggerFactory

import scala.concurrent.duration.{FiniteDuration, _}

case class SearchService(execution: Execution, client: ElasticClient, settings: SearchSettings) {

  private val Logger = LoggerFactory.getLogger(classOf[SearchService])

  import SearchService._
  import execution._

  def index(entry: FastaEntry, refresh: Boolean = false): IO[Response[IndexResponse]] = {
    if (refresh) {
      client.execute(indexRequest(entry, settings).refreshImmediately)
    } else {
      client.execute(indexRequest(entry, settings))
    }
  }

  def index(entries: List[FastaEntry]): IO[Response[BulkResponse]] = {
    val indexRequests = entries map { e => indexRequest(e, settings) }
    client.execute { bulk(indexRequests) }
  }

  def find(sequence: String): IO[Response[SearchResponse]] = {
    client.execute { search(settings.sequenceIndex) query sequence }
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

    retryWithBackOff(client.execute { clusterHealth() }, 5.seconds, 100)
  }

  def createSequenceIndex(): IO[Response[CreateIndexResponse]] = client.execute {
    createIndex(settings.sequenceIndex).mapping(MappingDefinition().as(
      TextField("header").analyzer("header_analyzer"),
      TextField("sequence").analyzer("sequence_analyzer")))
      .analysis(Analysis(
        analyzers = List(
          StandardAnalyzer("header_analyzer"),
          CustomAnalyzer("sequence_analyzer", "test", List.empty, List.empty)),
        tokenizers = List(NGramTokenizer("test", 3, 4))
      ))
  }

}

object SearchService {

  def instance =
    for {
      ex <- Env.execution
      search <- Env.search
    } yield SearchService(ex, search.client, search.settings)

  private def indexRequest(entry: FastaEntry, settings: SearchSettings) = {
    indexInto(settings.sequenceIndex) fields(
      "header" -> entry.header,
      "sequence" -> entry.sequence
    )
  }

}
