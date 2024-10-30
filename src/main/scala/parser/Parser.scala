package parser

import scala.annotation.targetName

/***
 * Representation of a Monadic Parser Combinators,
 * based on Graham Hutton and Erik Meijer paper
 * https://people.cs.nott.ac.uk/pszgmh/monparsing.pdf
 *
 * They used a list, and plus allows for 2 valid results...
 * then they proceed to use a StateMonad as input instead of plain string
 * but in this one we use '|' to choose a path as exclusive to each option,
 * and we use an Either as our Monad just to represent the Error
 * @tparam A
 */
type Parser[A] = String => Either[ParserError, (A, String)]

// Parser combinators
extension [A](p: Parser[A])
  // bind: integrates the sequencing of parsers with the processing of their result values
  // we normally call it flatMap in Scala and other languages
  def flatMap[B](f: A => Parser[B]): Parser[B] = input =>
    p(input) match
      case Left(error) => Left(error)
      case Right((parsed, unparsed)) => f(parsed)(unparsed)

  // seq:
  def map[B](f: A => B): Parser[B] =
    p.flatMap(a => Parser.result(f(a)))

  // Combine two parser, as an alternative
  @targetName("combineK")
  def |(p2: Parser[A]): Parser[A] = input =>
    p(input) match
      case Left(_) => p2(input)
      case result => result

object Parser:
  // Primitive parsers
  // ------
  // result: succeeds without consuming any of the input string, and returns the single result A
  // in scala we normally call it `pure`
  def result[A](in: A): Parser[A] = input => Right((in, input))

  // Dually, the parser zero always fails, regardless of the input string
  def zero[A]: Parser[A] = _ => Left(ParserError.ZeroFailure)
  
  // Parser that always fails with the given error
  def error[A](error: ParserError): Parser[A] = _ => Left(error)

  // successfully consumes the first character if the input string is non-empty, and fails otherwise
  def item: Parser[Char] =
    case "" => Left(ParserError.EarlyTermination)
    case str => Right((str.head, str.tail))

  // sat that takes a predicate (a Boolean valued function), and yields a parser that consumes a single character if it
  // satisfies the predicate
  def sat(condition: Char => Boolean): Parser[Char] =
    for
      x <- item
      x <- if condition(x) then result(x) else zero
    yield x

  def char(c: Char): Parser[Char] = sat(_ == c)
  def digit: Parser[Char] = sat(_.isDigit)
  def lower: Parser[Char] = sat(_.isLower)
  def upper: Parser[Char] = sat(_.isUpper)
  def whitespace: Parser[Char] = sat(_.isWhitespace)

  def letter: Parser[Char] = lower | upper
  def alphanum: Parser[Char] = letter | digit

  def word: Parser[String] =
    for
      x <- letter
      xs <- word | result("")
    yield s"$x$xs"

  // The combinator many applies a parser p zero or more times to an input string.
  // The results from each application of p are returned in a list:
  def many[A](p: Parser[A]): Parser[List[A]] = (
    for
      x <- p
      xs <- many(p)
    yield x :: xs
  ) | result(List())

  // many1 Sometimes we will only be interested in non-empty sequences of items.
  // For this reason we define a special combinator, many1, in terms of many
  def many1[A](p: Parser[A]): Parser[List[A]] =
    for
      x <- p
      xs <- many(p)
    yield x :: xs

  // Natural numbers
  def nat: Parser[Int] =
    many1(digit).map(_.mkString.toInt)

  // Integer
  def int: Parser[Int] =
    (
      for
        _ <- char('-')
        n <- nat
      yield -n
    ) | nat

  // List of comma separated ints
  def ints: Parser[Seq[Int]] =
    sepby1(int, char(','))

  // Repetition with separators
  def sepby1[A, B](p: Parser[A], sep: Parser[B]): Parser[Seq[A]] =
    for {
      x <- p
      xs <- many(for {
        _ <- sep
        y <- p
      } yield y)
    } yield x :: xs

  def bracket[A, B, C](open: Parser[A], p: Parser[B], close: Parser[C]): Parser[B] =
    for
      _ <- open
      x <- p
      _ <- close
    yield x

enum ParserError:
  case ZeroFailure
  case EarlyTermination
  case InvalidTransformation(label: String)