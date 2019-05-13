package io.beagle.directive

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchSource
import akka.stream.scaladsl.Sink
import io.beagle.Env
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import spray.json.DefaultJsonProtocol._

import scala.util._

object Search {

  val searchController = Env.env map { e =>
    implicit val esClient = RestClient
      .builder(new HttpHost(e.settings.elasticSearch.elasticSearchHost, e.settings.elasticSearch.elasticSearchPort))
      .build()
    path("search") {
      post {
        entity(as[String]) { searchSequence =>
          val q = s"""{"match": { "sequence": "$searchSequence"}}"""
          val readFuture = ElasticsearchSource(indexName = "fasta", typeName = "_doc", query = q).runWith(Sink.seq)(e.materializer)
          onComplete(readFuture) {
            case Success(value) => complete(value.map(_.source))
            case Failure(ex) => complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }

}
