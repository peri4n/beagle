package io.beagle.components.settings

import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}

sealed trait ElasticSearchSettings {
  def client: ElasticClient

  def sequenceIndex: String = "fasta"

  def protocol: String

  def host: String

  def port: Int
}

object ElasticSearchSettings {

  case class Local(protocol: String = "http", host: String = "localhost", port: Int = 9200) extends ElasticSearchSettings {
    val client = ElasticClient(JavaClient(ElasticProperties(s"${ protocol }://${ host }:${ port }")))
  }

}


