package io.beagle.search.docs

import com.sksamuel.elastic4s.requests.analysis.{
  Analysis => EsAnalysis, CustomAnalyzer, NGramTokenizer, StandardAnalyzer
}
import com.sksamuel.elastic4s.requests.mappings.{MappingDefinition, TextField}

case class FastaDoc(identifier: String,
                    projectId: Int,
                    sequence: String)

object FastaDoc {

  val headerFieldName = "header"

  val headerAnalyzer = s"${ headerFieldName }_analyzer"

  val sequenceFieldName = "sequence"

  val sequenceAnalyzer = s"${ sequenceFieldName }_analyzer"

  val Mapping = MappingDefinition().as(
    TextField(headerFieldName).analyzer(headerAnalyzer),
    TextField(sequenceFieldName).analyzer(sequenceAnalyzer))

  val Analysis = EsAnalysis(
    analyzers = List(
      StandardAnalyzer(headerAnalyzer),
      CustomAnalyzer(sequenceAnalyzer, "ngram_ref", List.empty, List.empty)),
    tokenizers = List(
      NGramTokenizer("ngram_ref", 3, 4))
  )

}
