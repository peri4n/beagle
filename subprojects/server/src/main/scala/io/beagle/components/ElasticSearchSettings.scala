package io.beagle.components

import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}

trait ElasticSearchSettings {
  val sequenceIndex: String = "fasta"

  val sequenceMapping: String = "sequence"

  val protocol: String

  val host: String

  val port: Int

  lazy val client = ElasticClient(JavaClient(ElasticProperties(s"${ protocol }://${ host }:${ port }")))

}

object ElasticSearchSettings {

  case class LocalElasticSearchSettings(protocol: String = "http", host: String = "localhost", port: Int = 9200) extends ElasticSearchSettings

  def local(protocol: String = "http", host: String = "localhost", port: Int = 9200) = LocalElasticSearchSettings(protocol, host, port)
}
