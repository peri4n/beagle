object FastaParser {

  def parse(content: String): List[FastaEntry] = {
    content.split(">")
      .tail
      .map(entry => entry.span(_ != '\n'))
      .map(FastaEntry.tupled)
      .toList
  }

}

case class FastaEntry(header: String, sequence: String)