package io.beagle.testsupport

import cats.MonadError
import cats.data.EitherT
import cats.effect.IO
import org.http4s.headers.{`Content-Encoding`, `Content-Type`}
import org.http4s.util.CaseInsensitiveString
import org.http4s._
import org.specs2.matcher.{IOMatchers, Matcher, Matchers, ValueCheck}

trait ResponseMatchers extends Matchers with IOMatchers {
  def haveStatus(expected: Status): Matcher[Response[IO]] =
    be_===(expected) ^^ { r: Response[IO] =>
      r.status.aka("the response status")
    }

  def returnStatus(s: Status): Matcher[IO[Response[IO]]] =
    haveStatus(s) ^^ { r: IO[Response[IO]] =>
      runAwait(r).aka("the returned")
    }

  def haveBody[A](a: ValueCheck[A])(
    implicit F: MonadError[IO, Throwable],
    ee: EntityDecoder[IO, A]): Matcher[Message[IO]] =
    returnValue(a) ^^ { m: Message[IO] =>
      m.as[A].aka("the message body")
    }

  def returnBody[A](a: ValueCheck[A])(
    implicit F: MonadError[IO, Throwable],
    ee: EntityDecoder[IO, A]): Matcher[IO[Message[IO]]] =
    returnValue(a) ^^ { m: IO[Message[IO]] =>
      m.flatMap(_.as[A]).aka("the returned message body")
    }

  def haveHeaders(a: Headers): Matcher[Message[IO]] =
    be_===(a) ^^ { m: Message[IO] =>
      m.headers.aka("the headers")
    }

  def containsHeader(h: Header): Matcher[Message[IO]] =
    beSome(h.value) ^^ { m: Message[IO] =>
      m.headers.get(h.name).map(_.value).aka("the particular header")
    }

  def doesntContainHeader(h: CaseInsensitiveString): Matcher[Message[IO]] =
    beNone ^^ { m: Message[IO] =>
      m.headers.get(h).aka("the particular header")
    }

  def haveMediaType(mt: MediaType): Matcher[Message[IO]] =
    beSome(mt) ^^ { m: Message[IO] =>
      m.headers.get(`Content-Type`).map(_.mediaType).aka("the media type header")
    }

  def haveContentCoding(c: ContentCoding): Matcher[Message[IO]] =
    beSome(c) ^^ { m: Message[IO] =>
      m.headers.get(`Content-Encoding`).map(_.contentCoding).aka("the content encoding header")
    }

  def returnRight[A, B](m: ValueCheck[B]): Matcher[EitherT[IO, A, B]] =
    beRight(m) ^^ { et: EitherT[IO, A, B] =>
      runAwait(et.value).aka("the either task")
    }

  def returnLeft[A, B](m: ValueCheck[A]): Matcher[EitherT[IO, A, B]] =
    beLeft(m) ^^ { et: EitherT[IO, A, B] =>
      runAwait(et.value).aka("the either task")
    }
}
