package io.beagle.service

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.Generators._
import io.beagle.components.Services
import io.beagle.domain.Seq
import io.beagle.environments.TestEnv
import io.beagle.service.SeqService.{SequenceAlreadyExists, SequenceDoesNotExist}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

class SeqServiceSpec extends FunSpec with GeneratorDrivenPropertyChecks with Matchers {

  val environment = TestEnv.of[SeqService]

  val service = Services.sequence(environment)

  def run[A](cio: ConnectionIO[A]): A = {
    cio.transact(environment.settings.database.transactor).unsafeRunSync()
  }

  describe("Creating sequences") {
    it("can create new sequences") {
      forAll { sequence: Seq =>
        run(service.create(sequence)).seq should be(sequence)
      }
    }

    it("fails if a sequence with the same name is already present") {
      forAll { sequence: Seq =>
        val test = for {
          _ <- service.create(sequence)
          _ <- service.create(sequence)
        } yield ()

        an[SequenceAlreadyExists] should be thrownBy run(test)
      }
    }
  }

  describe("Updating sequences") {
    it("can update existing sequences") {
      forAll { sequence: Seq =>
        val test = for {
          _ <- service.create(sequence)
          newItem <- service.update(sequence, sequence.copy(identifier = "foo"))
        } yield newItem

        run(test).seq should be(sequence.copy(identifier = "foo"))
      }
    }
  }

  it("can delete an already present sequence") {
    forAll { sequence: Seq =>
      val test = for {
        _ <- service.create(sequence)
        _ <- service.delete(sequence)
      } yield ()

      run(test)
    }
  }

  it("fails to delete if the sequence is not present") {
    forAll { seq: Seq =>
      val test = service.delete(seq)

      an[SequenceDoesNotExist] should be thrownBy run(test)
    }
  }
}
