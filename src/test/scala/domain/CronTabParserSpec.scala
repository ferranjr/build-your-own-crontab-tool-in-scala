package domain

import domain.CronExpr.Every
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import parser.{CronTabParser, ParserError}

class CronTabParserSpec
  extends AnyFlatSpec
    with Matchers
    with EitherValues {

  "CronTabParser" should "parse an every type of expression" in {
    CronTabParser.every("*/10").value shouldBe (CronExpr.Every(10), "")
    CronTabParser.cronTabExpression("*/10").value shouldBe (CronExpr.Every(10), "")
  }

  it should "parse a range type expression" in {
    CronTabParser.range("1-10").value shouldBe (CronExpr.Range(1, 10), "")
    CronTabParser.cronTabExpression("1-10").value shouldBe (CronExpr.Range(1, 10), "")
  }

  it should "parse a atTimes type expression" in {
    CronTabParser.atTimes("1,2,3,4").value shouldBe (CronExpr.AtTimes(Seq(1,2,3,4)), "")
    CronTabParser.cronTabExpression("1,2,3,4").value shouldBe (CronExpr.AtTimes(Seq(1,2,3,4)), "")
  }

  it should "parse anyTime type expression" in {
    CronTabParser.anyTime("*").value shouldBe (CronExpr.AnyTime, "")
    CronTabParser.cronTabExpression("*").value shouldBe (CronExpr.AnyTime, "")
  }

  "minute" should "parser a valid Minute" in {
    CronTabParser.minute("*/15").value shouldBe (Minute(Every(15)), "")
    CronTabParser.minute("1-15").value shouldBe (Minute(CronExpr.Range(1, 15)), "")
    CronTabParser.minute("1,5,56").value shouldBe (Minute(CronExpr.AtTimes(Seq(1, 5, 56))), "")
    CronTabParser.minute("5").value shouldBe (Minute(CronExpr.AtTimes(Seq(5))), "")
  }

  it should "fail to parse a Minute with values out of range" in {
    CronTabParser.minute("*/65").left.value shouldBe ParserError.InvalidTransformation("minute")
    CronTabParser.minute("1-185").left.value shouldBe ParserError.InvalidTransformation("minute")
    CronTabParser.minute("15-1").left.value shouldBe ParserError.InvalidTransformation("minute")
    CronTabParser.minute("1,5,102").left.value shouldBe ParserError.InvalidTransformation("minute")
    CronTabParser.minute("90").left.value shouldBe ParserError.InvalidTransformation("minute")
  }

  "hour" should "parser a valid Hour" in {
    CronTabParser.hour("*/15").value shouldBe (Hour(Every(15)), "")
    CronTabParser.hour("1-15").value shouldBe (Hour(CronExpr.Range(1, 15)), "")
    CronTabParser.hour("1,5,18").value shouldBe (Hour(CronExpr.AtTimes(Seq(1, 5, 18))), "")
    CronTabParser.hour("5").value shouldBe (Hour(CronExpr.AtTimes(Seq(5))), "")
  }

  it should "fail to parse a Minute with values out of range" in {
    CronTabParser.hour("*/24").left.value shouldBe ParserError.InvalidTransformation("hour")
    CronTabParser.hour("1-185").left.value shouldBe ParserError.InvalidTransformation("hour")
    CronTabParser.hour("15-1").left.value shouldBe ParserError.InvalidTransformation("hour")
    CronTabParser.hour("1,5,102").left.value shouldBe ParserError.InvalidTransformation("hour")
    CronTabParser.hour("25").left.value shouldBe ParserError.InvalidTransformation("hour")
  }

  "dayOfTheMonth" should "parser a valid DayOfTheMonth" in {
    CronTabParser.dayOfTheMonth("*/15").value shouldBe (DayOfTheMonth(Every(15)), "")
    CronTabParser.dayOfTheMonth("1-15").value shouldBe (DayOfTheMonth(CronExpr.Range(1, 15)), "")
    CronTabParser.dayOfTheMonth("1,5,18").value shouldBe (DayOfTheMonth(CronExpr.AtTimes(Seq(1, 5, 18))), "")
    CronTabParser.dayOfTheMonth("5").value shouldBe (DayOfTheMonth(CronExpr.AtTimes(Seq(5))), "")
  }

  it should "fail to parse a Minute with values out of range" in {
    CronTabParser.dayOfTheMonth("*/32").left.value shouldBe ParserError.InvalidTransformation("dayOfTheMonth")
    CronTabParser.dayOfTheMonth("1-185").left.value shouldBe ParserError.InvalidTransformation("dayOfTheMonth")
    CronTabParser.dayOfTheMonth("15-1").left.value shouldBe ParserError.InvalidTransformation("dayOfTheMonth")
    CronTabParser.dayOfTheMonth("1,5,102").left.value shouldBe ParserError.InvalidTransformation("dayOfTheMonth")
    CronTabParser.dayOfTheMonth("901").left.value shouldBe ParserError.InvalidTransformation("dayOfTheMonth")
  }

  "month" should "parser a valid Month" in {
    CronTabParser.month("*/2").value shouldBe (Month(Every(2)), "")
    CronTabParser.month("1-10").value shouldBe (Month(CronExpr.Range(1, 10)), "")
    CronTabParser.month("JAN-OCT").value shouldBe (Month(CronExpr.RangeLabels("JAN", "OCT")), "")
    CronTabParser.month("JAN,MAR").value shouldBe (Month(CronExpr.AtTimesLabels(Seq("JAN", "MAR"))), "")
    CronTabParser.month("1,5,12").value shouldBe (Month(CronExpr.AtTimes(Seq(1, 5, 12))), "")
    CronTabParser.month("5").value shouldBe (Month(CronExpr.AtTimes(Seq(5))), "")
  }

  it should "fail to parse a Minute with values out of range" in {
    CronTabParser.month("*/32").left.value shouldBe ParserError.InvalidTransformation("month")
    CronTabParser.month("1-185").left.value shouldBe ParserError.InvalidTransformation("month")
    CronTabParser.month("15-1").left.value shouldBe ParserError.InvalidTransformation("month")
    CronTabParser.month("1,5,102").left.value shouldBe ParserError.InvalidTransformation("month")
    CronTabParser.month("901").left.value shouldBe ParserError.InvalidTransformation("month")
    CronTabParser.month("JANUARY").left.value shouldBe ParserError.InvalidTransformation("month")
    CronTabParser.month("FOOBAR").left.value shouldBe ParserError.InvalidTransformation("month")
  }

  "dayOfTheWeek" should "parser a valid DayOfTheWeek" in {
    CronTabParser.dayOfTheWeek("*/2").value shouldBe (DayOfTheWeek(Every(2)), "")
    CronTabParser.dayOfTheWeek("1-5").value shouldBe (DayOfTheWeek(CronExpr.Range(1, 5)), "")
    CronTabParser.dayOfTheWeek("MON-FRI").value shouldBe (DayOfTheWeek(CronExpr.RangeLabels("MON", "FRI")), "")
    CronTabParser.dayOfTheWeek("0,5,6").value shouldBe (DayOfTheWeek(CronExpr.AtTimes(Seq(0, 5, 6))), "")
    CronTabParser.dayOfTheWeek("SUN,FRI,SAT").value shouldBe (DayOfTheWeek(CronExpr.AtTimesLabels(Seq("SUN","FRI","SAT"))), "")
    CronTabParser.dayOfTheWeek("5").value shouldBe (DayOfTheWeek(CronExpr.AtTimes(Seq(5))), "")
  }

  it should "fail to parse a Minute with values out of range" in {
    CronTabParser.dayOfTheWeek("*/32").left.value shouldBe ParserError.InvalidTransformation("dayOfTheWeek")
    CronTabParser.dayOfTheWeek("1-185").left.value shouldBe ParserError.InvalidTransformation("dayOfTheWeek")
    CronTabParser.dayOfTheWeek("15-1").left.value shouldBe ParserError.InvalidTransformation("dayOfTheWeek")
    CronTabParser.dayOfTheWeek("1,5,102").left.value shouldBe ParserError.InvalidTransformation("dayOfTheWeek")
    CronTabParser.dayOfTheWeek("901").left.value shouldBe ParserError.InvalidTransformation("dayOfTheWeek")
    CronTabParser.dayOfTheWeek("MONDAY").left.value shouldBe ParserError.InvalidTransformation("dayOfTheWeek")
    CronTabParser.dayOfTheWeek("FOOBAR").left.value shouldBe ParserError.InvalidTransformation("dayOfTheWeek")
  }

  "CronTabParser.parse" should "parse input string correctly" in {
    CronTabParser
      .parse("* * * * * /foo/bar/baz > boom")
      .value shouldBe CronTabExpression(
      Minute(CronExpr.AnyTime),
      Hour(CronExpr.AnyTime),
      DayOfTheMonth(CronExpr.AnyTime),
      Month(CronExpr.AnyTime),
      DayOfTheWeek(CronExpr.AnyTime),
      "/foo/bar/baz > boom"
    )
  }

  it should "parse input string correctly example 2" in {
    CronTabParser
      .parse("*/15 * * * 1-5 /foo/bar/baz > boom")
      .value shouldBe CronTabExpression(
      Minute(CronExpr.Every(15)),
      Hour(CronExpr.AnyTime),
      DayOfTheMonth(CronExpr.AnyTime),
      Month(CronExpr.AnyTime),
      DayOfTheWeek(CronExpr.Range(1, 5)),
      "/foo/bar/baz > boom"
    )
  }

  it should "parse input string correctly example 3" in {
    CronTabParser
      .parse("*/15 * * JAN-JUL 1-5 /foo/bar/baz > boom")
      .value shouldBe CronTabExpression(
      Minute(CronExpr.Every(15)),
      Hour(CronExpr.AnyTime),
      DayOfTheMonth(CronExpr.AnyTime),
      Month(CronExpr.RangeLabels("JAN", "JUL")),
      DayOfTheWeek(CronExpr.Range(1, 5)),
      "/foo/bar/baz > boom"
    )
  }

  it should "fail to parse input string for invalid number" in {
    CronTabParser
      .parse("80 * * * * /foo/bar/baz > boom").left.value shouldBe ParserError.InvalidTransformation("minute")
    CronTabParser
      .parse("*/5 24 * * * /foo/bar/baz > boom").left.value shouldBe ParserError.InvalidTransformation("hour")
    CronTabParser
      .parse("*/5 0 32 * * /foo/bar/baz > boom").left.value shouldBe ParserError.InvalidTransformation("dayOfTheMonth")
    CronTabParser
      .parse("*/5 0 31 13 * /foo/bar/baz > boom").left.value shouldBe ParserError.InvalidTransformation("month")
    CronTabParser
      .parse("*/5 0 31 12 7 /foo/bar/baz > boom").left.value shouldBe ParserError.InvalidTransformation("dayOfTheWeek")
  }
}
