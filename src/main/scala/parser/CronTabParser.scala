package parser

import domain.*
import parser.ParserError.InvalidTransformation

import scala.util.Try


object CronTabParser:

  // */5
  def every: Parser[CronExpr] =
    for
      _ <- Parser.char('*')
      _ <- Parser.char('/')
      v <- Parser.nat
    yield CronExpr.Every(v)

  // 1-6
  def range: Parser[CronExpr] =
    for
      min <- Parser.nat
      _ <- Parser.char('-')
      max <- Parser.nat
    yield CronExpr.Range(min, max)

  def rangeLabels: Parser[CronExpr] =
    for
      min <- Parser.word
      _ <- Parser.char('-')
      max <- Parser.word
    yield CronExpr.RangeLabels(min, max)

  // 1,2,3
  def atTimes: Parser[CronExpr] =
    Parser.sepby1(Parser.nat, Parser.char(',')).map(CronExpr.AtTimes.apply)

  def atTimesLabels: Parser[CronExpr] =
    Parser.sepby1(Parser.word, Parser.char(',')).map(CronExpr.AtTimesLabels.apply)

  // *
  def anyTime: Parser[CronExpr] =
    Parser.char('*').map(_ => CronExpr.AnyTime)

  def cronTabExpression: Parser[CronExpr] =
    every | range | atTimes | anyTime | rangeLabels | atTimesLabels

  def command: Parser[String] = input => Right((input, ""))

  extension [A](p: Parser[A])
    def tryTransformation[B](f: A => B, label: String): Parser[B] =
      for
        x <- p
        y <- Try(f(x)).fold(_ => Parser.error(InvalidTransformation(label)), Parser.result)
      yield y

  def minute: Parser[Minute] =
    cronTabExpression.tryTransformation(Minute.apply, "minute")

  def hour: Parser[Hour] =
    cronTabExpression.tryTransformation(Hour.apply, "hour")

  def dayOfTheMonth: Parser[DayOfTheMonth] =
    cronTabExpression.tryTransformation(DayOfTheMonth.apply, "dayOfTheMonth")

  def month: Parser[Month] =
    cronTabExpression.tryTransformation(Month.apply, "month")

  def dayOfTheWeek: Parser[DayOfTheWeek] =
    cronTabExpression.tryTransformation(DayOfTheWeek.apply, "dayOfTheWeek")

  /**
   *
   * @return
   */
  def fullCronTab: Parser[CronTabExpression] =
    for {
      minute <- minute
      _ <- Parser.whitespace
      hour <- hour
      _ <- Parser.whitespace
      dayOfTheMonth <- dayOfTheMonth
      _ <- Parser.whitespace
      month <- month
      _ <- Parser.whitespace
      dayOfTheWeek <- dayOfTheWeek
      _ <- Parser.whitespace
      cmd <- command
    } yield CronTabExpression(
      minute,
      hour,
      dayOfTheMonth,
      month,
      dayOfTheWeek,
      cmd
    )

  def parse(input: String): Either[ParserError, CronTabExpression] =
    fullCronTab(input).map(_._1)