package io.beagle.service

import cats.effect.{IO, Timer}
import cats.implicits._
import com.sksamuel.elastic4s.Response
import com.sksamuel.elastic4s.cats.effect.instances._
import com.sksamuel.elastic4s.requests.bulk.BulkResponse
import com.sksamuel.elastic4s.requests.cluster.ClusterHealthResponse
import com.sksamuel.elastic4s.requests.indexes.{CreateIndexResponse, IndexResponse}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.requests.analysis.{Analysis, CustomAnalyzer, NGramTokenizer, StandardAnalyzer}
import com.sksamuel.elastic4s.requests.mappings.{MappingDefinition, TextField}
import com.sksamuel.elastic4s.requests.searches.SearchResponse
import io.beagle.components.Settings
import io.beagle.components.settings.ElasticSearchSettings
import io.beagle.fasta.FastaEntry

import scala.concurrent.duration.{FiniteDuration, _}

case class ElasticSearchService(settings: ElasticSearchSettings) {

  import ElasticSearchService._

  def index(entry: FastaEntry, refresh: Boolean = false): IO[Response[IndexResponse]] = {
    if (refresh) {
      settings.client.execute(indexRequest(entry, settings).refreshImmediately)
    } else {
      settings.client.execute(indexRequest(entry, settings))
    }
  }

  def index(entries: List[FastaEntry]): IO[Response[BulkResponse]] = {
    val indexRequests = entries map { e => indexRequest(e, settings) }
    settings.client.execute { bulk(indexRequests) }
  }

  def find(sequence: String): IO[Response[SearchResponse]] = {
    settings.client.execute { search(settings.sequenceIndex) query sequence }
  }

  def connectionCheck()(implicit timer: Timer[IO]): IO[Response[ClusterHealthResponse]] = {
    def retryWithBackOff[A](ioa: IO[A], initialDelay: FiniteDuration, maxRetries: Int)(implicit timer: Timer[IO]): IO[A] = {
      ioa.handleErrorWith { error =>
        if (maxRetries > 0)
          IO.sleep(initialDelay) *> retryWithBackOff(ioa, initialDelay * 2, maxRetries - 1)
        else
          IO.raiseError(error)
      }
    }

    retryWithBackOff(settings.client.execute { clusterHealth() }, 5.seconds, 100)
  }

  def createSequenceIndex(): IO[Response[CreateIndexResponse]] = settings.client.execute {
    createIndex(settings.sequenceIndex).mapping(MappingDefinition().as(
      TextField("header").analyzer("header_analyzer"),
      TextField("sequence").analyzer("sequence_analyzer")))
      .analysis(Analysis(
        analyzers = List(
          StandardAnalyzer("header_analyzer"),
          CustomAnalyzer( "sequence_analyzer", "test", List.empty, List.empty)),
        tokenizers = List(NGramTokenizer("test", 3, 4))
      ))
  }

}

object ElasticSearchService {

  def instance = Settings.elasticSearch map { ElasticSearchService(_) }

  private def indexRequest(entry: FastaEntry, settings: ElasticSearchSettings) = {
    indexInto(settings.sequenceIndex) fields(
      "header" -> entry.header,
      "sequence" -> entry.sequence
    )
  }

}
