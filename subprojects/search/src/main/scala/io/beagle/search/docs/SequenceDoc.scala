package io.beagle.search.docs

import com.sksamuel.elastic4s.requests.analysis.{CustomAnalyzer, NGramTokenizer, StandardAnalyzer, Analysis => EsAnalysis}
import com.sksamuel.elastic4s.requests.mappings.{KeywordField, MappingDefinition, TextField}

case class SequenceDoc(header: String,
                       datasetId: Long,
                       sequence: String)

object SequenceDoc {

  val headerFieldName = "header"

  val headerAnalyzer = s"${ headerFieldName }_analyzer"

  val sequenceFieldName = "sequence"

  val sequenceAnalyzer = s"${ sequenceFieldName }_analyzer"

  val datasetIdFieldName = "datasetId"

  val datasetIdFieldAnalyzer = s"${ datasetIdFieldName }_analyzer"

  val Mapping = MappingDefinition().as(
    TextField(headerFieldName).analyzer(headerAnalyzer),
    TextField(sequenceFieldName).analyzer(sequenceAnalyzer),
    KeywordField(datasetIdFieldName)
  )

  val Analysis = EsAnalysis(
    analyzers = List(
      StandardAnalyzer(headerAnalyzer),
      CustomAnalyzer(sequenceAnalyzer, "ngram_ref", List.empty, List.empty)),
    tokenizers = List(
      NGramTokenizer("ngram_ref", 3, 4))
  )

}
