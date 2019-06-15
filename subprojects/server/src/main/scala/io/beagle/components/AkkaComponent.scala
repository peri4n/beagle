package io.beagle.components

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait AkkaComponent {

  def system: ActorSystem = ActorSystem("my-system")

  def materializer: ActorMaterializer = ActorMaterializer()(system)

}
