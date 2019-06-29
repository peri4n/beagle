package io.beagle.service

import cats.effect.IO
import com.sksamuel.elastic4s.analyzers.{CustomAnalyzerDefinition, NGramTokenizer, StandardAnalyzer, UppercaseTokenFilter}
import com.sksamuel.elastic4s.cats.effect.instances._
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.Response
import com.sksamuel.elastic4s.http.bulk.BulkResponse
import com.sksamuel.elastic4s.http.cluster.ClusterHealthResponse
import com.sksamuel.elastic4s.http.index.{CreateIndexResponse, IndexResponse}
import com.sksamuel.elastic4s.http.search.SearchResponse
import com.sksamuel.elastic4s.mappings.{MappingDefinition, TextField}
import io.beagle.components.{ElasticSearchSettings, Settings}
import io.beagle.fasta.FastaEntry

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

  def connectionCheck(): IO[Response[ClusterHealthResponse]] = {
    settings.client.execute { clusterHealth() }
  }

  def createSequenceIndex(): IO[Response[CreateIndexResponse]] = settings.client.execute {
    createIndex(settings.sequenceIndex) mappings {
      MappingDefinition(settings.sequenceMapping).as(
        TextField("header").analyzer(StandardAnalyzer),
        TextField("sequence").analyzer("custom"))
    } analysis {
      CustomAnalyzerDefinition(
        "custom",
        NGramTokenizer("nGram", 3, 5),
        UppercaseTokenFilter
      )
    }
  }

}

object ElasticSearchService {

  val instance = Settings.elasticSearch map { ElasticSearchService(_) }

  private def indexRequest(entry: FastaEntry, settings: ElasticSearchSettings) = {
    indexInto(settings.sequenceIndex, settings.sequenceMapping) fields(
      "header" -> entry.header,
      "sequence" -> entry.sequence
    )
  }

}
