package domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.LocalDateTime

class CronTabExpressionSpec
  extends AnyFlatSpec
    with Matchers {

  behavior of "CronTabExpression"

  "toNaturalLanguage" should "convert expression to a human natural language" in {
    val cronTabExpression = CronTabExpression(
      Minute(CronExpr.AnyTime),
      Hour(CronExpr.Range(9, 10)),
      DayOfTheMonth(CronExpr.AnyTime),
      Month(CronExpr.AnyTime),
      DayOfTheWeek(CronExpr.AtTimes(List(6))),
      ""
    )
    val expected = "Every minute, between 09:00 AM and 09:59 AM, only on Saturdays"
    cronTabExpression.toNaturalLanguage shouldBe expected
  }

  it should "convert expression to a human natural language example 2" in {
    val cronTabExpression = CronTabExpression(
      Minute(CronExpr.Every(5)),
      Hour(CronExpr.Range(9, 16)),
      DayOfTheMonth(CronExpr.AnyTime),
      Month(CronExpr.AnyTime),
      DayOfTheWeek(CronExpr.RangeLabels("MON", "FRI")),
      ""
    )
    val expected = "Every 5 minutes, between 09:00 AM and 03:59 PM, Monday through Friday"
    cronTabExpression.toNaturalLanguage shouldBe expected
  }

  "nextInstances" should "return the expected N entries" in {
    val cronExpression = CronTabExpression(
      Minute(CronExpr.Every(5)),
      Hour(CronExpr.AnyTime),
      DayOfTheMonth(CronExpr.AnyTime),
      Month(CronExpr.AnyTime),
      DayOfTheWeek(CronExpr.AnyTime),
      "foobar"
    )
    val result = cronExpression.nextInstances(n = 5, LocalDateTime.parse("2024-09-10T00:00:00"))
    result shouldBe List(
      LocalDateTime.parse("2024-09-10T00:00:00"),
      LocalDateTime.parse("2024-09-10T00:05:00"),
      LocalDateTime.parse("2024-09-10T00:10:00"),
      LocalDateTime.parse("2024-09-10T00:15:00"),
      LocalDateTime.parse("2024-09-10T00:20:00"),
    )
  }
  
  it should "display the every hour at minute 10" in {
    val cronExpression = CronTabExpression(
      Minute(CronExpr.AtTimes(List(10))),
      Hour(CronExpr.AnyTime),
      DayOfTheMonth(CronExpr.AnyTime),
      Month(CronExpr.AnyTime),
      DayOfTheWeek(CronExpr.AnyTime),
      "foobar"
    )
    val result = cronExpression.nextInstances(n = 5, LocalDateTime.parse("2024-09-10T00:00:00"))
    result shouldBe List(
      LocalDateTime.parse("2024-09-10T00:10:00"),
      LocalDateTime.parse("2024-09-10T01:10:00"),
      LocalDateTime.parse("2024-09-10T02:10:00"),
      LocalDateTime.parse("2024-09-10T03:10:00"),
      LocalDateTime.parse("2024-09-10T04:10:00"),
    )
  }
  
  it should "display the same hour every day" in {
    val cronExpression = CronTabExpression(
      Minute(CronExpr.AtTimes(List(0))),
      Hour(CronExpr.AtTimes(List(10))),
      DayOfTheMonth(CronExpr.AnyTime),
      Month(CronExpr.AnyTime),
      DayOfTheWeek(CronExpr.AnyTime),
      "foobar"
    )
    val result = cronExpression.nextInstances(n = 5, LocalDateTime.parse("2024-09-10T00:00:00"))
    result shouldBe List(
      LocalDateTime.parse("2024-09-10T10:00:00"),
      LocalDateTime.parse("2024-09-11T10:00:00"),
      LocalDateTime.parse("2024-09-12T10:00:00"),
      LocalDateTime.parse("2024-09-13T10:00:00"),
      LocalDateTime.parse("2024-09-14T10:00:00"),
    )
  }

  it should "display the once a month at same time" in {
    val cronExpression = CronTabExpression(
      Minute(CronExpr.AtTimes(List(10))),
      Hour(CronExpr.AtTimes(List(12))),
      DayOfTheMonth(CronExpr.AtTimes(List(15))),
      Month(CronExpr.AnyTime),
      DayOfTheWeek(CronExpr.AnyTime),
      "foobar"
    )
    val result = cronExpression.nextInstances(n = 5, LocalDateTime.parse("2024-09-10T00:00:00"))
    result shouldBe List(
      LocalDateTime.parse("2024-09-15T12:10:00"),
      LocalDateTime.parse("2024-10-15T12:10:00"),
      LocalDateTime.parse("2024-11-15T12:10:00"),
      LocalDateTime.parse("2024-12-15T12:10:00"),
      LocalDateTime.parse("2025-01-15T12:10:00"),
    )
  }

  it should "respect running only MON-FRI day of the week" in {
    val cronExpression = CronTabExpression(
      Minute(CronExpr.AtTimes(List(10))),
      Hour(CronExpr.AtTimes(List(12))),
      DayOfTheMonth(CronExpr.AtTimes(List(15))),
      Month(CronExpr.AnyTime),
      DayOfTheWeek(CronExpr.Range(1, 5)), // Monday to Friday
      "foobar"
    )
    val result = cronExpression.nextInstances(n = 5, LocalDateTime.parse("2024-09-10T00:00:00"))
    result shouldBe List(
      LocalDateTime.parse("2024-10-15T12:10:00"), // TUESDAY
      LocalDateTime.parse("2024-11-15T12:10:00"), // FRIDAY
      LocalDateTime.parse("2025-01-15T12:10:00"), // WEDNESDAY
      LocalDateTime.parse("2025-04-15T12:10:00"), // TUESDAY
      LocalDateTime.parse("2025-05-15T12:10:00"), // THURSDAY
    )
  }

  it should "respect end of month not being valid" in {
    val cronExpression = CronTabExpression(
      Minute(CronExpr.AtTimes(List(10))),
      Hour(CronExpr.AtTimes(List(12))),
      DayOfTheMonth(CronExpr.AtTimes(List(31))),
      Month(CronExpr.AnyTime),
      DayOfTheWeek(CronExpr.AnyTime),
      "foobar"
    )
    val result = cronExpression.nextInstances(n = 5, LocalDateTime.parse("2024-09-10T00:00:00"))
    result shouldBe List(
      LocalDateTime.parse("2024-10-31T12:10:00"),
      LocalDateTime.parse("2024-12-31T12:10:00"),
      LocalDateTime.parse("2025-01-31T12:10:00"),
      LocalDateTime.parse("2025-03-31T12:10:00"),
      LocalDateTime.parse("2025-05-31T12:10:00"),
    )
  }
}
