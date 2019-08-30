package io.beagle.components

import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}


trait ElasticSearchSettings {
  lazy val client = ElasticClient(JavaClient(ElasticProperties(s"${ protocol }://${ host }:${ port }")))

  def sequenceIndex: String = "fasta"

  def sequenceMapping: String = "sequence"

  def protocol: String

  def host: String

  def port: Int
}

object ElasticSearchSettings {

  def local(protocol: String = "http", host: String = "localhost", port: Int = 9200) = LocalElasticSearchSettings(protocol, host, port)

  case class LocalElasticSearchSettings(protocol: String = "http", host: String = "localhost", port: Int = 9200) extends ElasticSearchSettings

}
