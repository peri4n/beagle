package io.beagle.components.settings

sealed trait SearchSettings {
  def sequenceIndex: String = "fasta"

  def protocol: String

  def host: String

  def port: Int
}

object SearchSettings {

  case class Local(protocol: String = "http", host: String = "localhost", port: Int = 9200) extends SearchSettings

}


