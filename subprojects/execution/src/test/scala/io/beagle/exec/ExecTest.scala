package io.beagle.exec

import io.beagle.exec.Exec.{Fixed, Global}
import munit.FunSuite
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class ExecTest extends FunSuite {

  test("can be configured as a fixed thread pool.") {
    assertEquals(
      ConfigSource.resources("fixed.conf").load[Fixed],
      Right(Fixed(2)))
  }

  test("can be configured as the global thread pool.") {
    assertEquals(
      ConfigSource.resources("global.conf").load[Global.type],
      Right(Global))
  }
}
