package io.beagle.fasta

import cats.effect.IO
import fs2._
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

  def parseStream(entityBody: Stream[IO, Byte]): Stream[IO, FastaEntry] = {
    entityBody.split(_ == '>'.toByte)
      .tail
      .map(chunk => chunk.toString().span(_ != '\n'))
      .map { case (h, s) => (h, s.trim) }
      .map(FastaEntry.tupled)
  }

}

case class FastaEntry(header: String, sequence: String)
