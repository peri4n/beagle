package io.beagle.testsupport

import cats.effect.IO
import org.http4s.{EntityBody, Response, Status}
import org.scalatest.matchers.{HavePropertyMatchResult, HavePropertyMatcher}

trait ResponseMatchers {

  def status(expectedValue: Status) =
    new HavePropertyMatcher[Response[IO], Status] {
      def apply(response: Response[IO]) =
        HavePropertyMatchResult(
          response.status == expectedValue,
          "status",
          expectedValue,
          response.status
        )
    }

  def body(expectedValue: EntityBody[IO]) =
    new HavePropertyMatcher[Response[IO], EntityBody[IO]] {
      def apply(response: Response[IO]) =
        HavePropertyMatchResult(
          response.body == expectedValue,
          "body",
          expectedValue,
          response.body
        )
    }
}

