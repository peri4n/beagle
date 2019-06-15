package io.beagle.components

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait AkkaComponent {

  val system: ActorSystem = ActorSystem("test-system")

  val materializer: ActorMaterializer = ActorMaterializer()(system)

}
