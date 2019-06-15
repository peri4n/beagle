package io.beagle.fasta

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient

object FastaParser {

  implicit val client: RestClient = RestClient.builder(new HttpHost("localhost", 9200)).build()

  def parse(content: String): List[FastaEntry] = {
    content.split(">")
      .tail
      .map(entry => entry.span(_ != '\n'))
      .map { case (h, s) => (h, s.trim) }
      .map(FastaEntry.tupled)
      .toList
  }

}

case class FastaEntry(header: String, sequence: String)
