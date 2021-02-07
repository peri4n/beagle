package io.beagle.persistence

import io.beagle.persistence.testsupport.LocalPostgres
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, TryValues}

class PostgresDBTest extends LocalPostgres with Matchers with TryValues with BeforeAndAfter {

}
