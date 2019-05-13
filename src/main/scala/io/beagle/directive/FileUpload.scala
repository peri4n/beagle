package io.beagle.directive

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.alpakka.elasticsearch.WriteMessage
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchSink
import akka.stream.scaladsl.Source
import io.beagle.Env
import io.beagle.fasta.FastaParser
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import spray.json.DefaultJsonProtocol._

import scala.util._

object FileUpload {

  val uploadController = Env.env map { env =>

    import FastaParser.fastaEntryJsonFormat

    implicit val esClient = RestClient
      .builder(new HttpHost(env.settings.elasticSearch.elasticSearchHost, env.settings.elasticSearch.elasticSearchPort))
      .build()

    path("upload") {
      post {
        entity(as[String]) { fileUpload =>

          val entries = FastaParser.parse(fileUpload)
          val messages = entries.map(entry => WriteMessage.createIndexMessage(source = entry))
          val writeFuture = Source(messages)
            .runWith(ElasticsearchSink.create("fasta", typeName = "_doc"))(env.materializer)
          onComplete(writeFuture) {
            case Success(value) => complete(Map("status" -> "success"))
            case Failure(ex) => complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
          }
        }
      }
    }
  }
}
