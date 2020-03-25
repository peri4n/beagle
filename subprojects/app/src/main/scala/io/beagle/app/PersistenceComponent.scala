package io.beagle.app

import io.beagle.persistence.Persistence

trait PersistenceComponent {

  def persistence: Persistence

}
