package utils

import domain.{CronExpr, CronTabExpression, DayOfTheMonth, DayOfTheWeek, Hour, Minute, Month}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import utils.Showable.given

class ShowableSpec
  extends AnyFlatSpec
    with Matchers {

  "Showable" should "display `CronTime` instances as list of whitespace separated integers" in {
    Minute(CronExpr.Every(5)).show shouldBe "0 5 10 15 20 25 30 35 40 45 50 55"
    Hour(CronExpr.Every(6)).show shouldBe "0 6 12 18"
    DayOfTheMonth(CronExpr.AtTimes(List(1, 7, 21))).show shouldBe "1 7 21"
    Month(CronExpr.AtTimes(List(1, 7, 11))).show shouldBe "1 7 11"
    DayOfTheWeek(CronExpr.AtTimes(List(0, 5))).show shouldBe "0 5"
  }

  it should "display `CronTabExpression` as per the specs" in {
    //0 9 * * SAT
    CronTabExpression(
      Minute(CronExpr.AtTimes(List(0))),
      Hour(CronExpr.AtTimes(List(9))),
      DayOfTheMonth(CronExpr.AnyTime),
      Month(CronExpr.AnyTime),
      DayOfTheWeek(CronExpr.AtTimesLabels(List("SAT"))),
      "/foo/bar > baz"
    ).show should contain
      s"""
         |minutes       0
         |hours         9
         |day of month  1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31
         |month         1 2 3 4 5 6 7 8 9 10 11 12
         |day of week   6
         |command       /foo/bar > baz
         |
         |At minutes 0, at 9 hours, on Saturday
         |""".stripMargin
  }

}
