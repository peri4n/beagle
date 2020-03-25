package io.beagle.domain

sealed trait Alphabet

case object DNA extends Alphabet

case object RNA extends Alphabet

case object Amino extends Alphabet
