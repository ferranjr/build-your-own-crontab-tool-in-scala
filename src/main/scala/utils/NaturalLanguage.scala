package utils

import domain.{CronExpr, DayOfTheWeek, Hour, Minute}

import java.time.format.{DateTimeFormatter, TextStyle}
import java.time.LocalTime
import java.util.Locale
import java.time.{DayOfWeek as JDayOfWeek}

trait NaturalLanguage[A]:
  extension (in: A)
    def naturalLanguage: String

object NaturalLanguage:

  given NaturalLanguage[Minute] with
    extension (in: Minute)
      def naturalLanguage: String = in.expr match
        case CronExpr.AnyTime | CronExpr.Every(1) => "Every minute"
        case CronExpr.Every(x) => s"Every $x minutes"
        case CronExpr.AtTimes(values) => s"At minutes ${values.mkString(",")}"
        case domain.CronExpr.Range(min, max) => s"Every minute $min through $max"
        case _ => s""

  private def timeFormatted(hour: Int, minutes: Int = 0): String = {
    val time = LocalTime.of(hour, minutes)
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    time.format(formatter).toUpperCase()
  }

  given NaturalLanguage[Hour] with
    extension (in: Hour)
      def naturalLanguage: String =
        in.expr match
          case CronExpr.AnyTime | CronExpr.Every(1) => "every hour"
          case CronExpr.Every(x) => s"every $x hours"
          case CronExpr.AtTimes(values) => s"at ${values.mkString(",")} hours"
          case domain.CronExpr.Range(min, max) => s"between ${timeFormatted(min)} and ${timeFormatted(max - 1, 59)}"
          case _ => s""

  private def fullNameDayOfTheWeek(int: Int): String =
    JDayOfWeek.of(if int == 0 then 7 else int)
      .getDisplayName(TextStyle.FULL, Locale.UK)


  given NaturalLanguage[DayOfTheWeek] with
    extension (in: DayOfTheWeek)
      def naturalLanguage: String = in.expr match
        case CronExpr.AnyTime | CronExpr.Every(1) => "every day"
        case CronExpr.Every(x) => s"every $x days"
        case CronExpr.AtTimes(value :: Nil) => s"only on ${fullNameDayOfTheWeek(value)}s"
        case CronExpr.AtTimes(values) => s"on ${values.map(fullNameDayOfTheWeek).mkString(",")}"
        case CronExpr.AtTimesLabels(values) => s"on ${values.map(in.labels).map(fullNameDayOfTheWeek).mkString(",")}"
        case domain.CronExpr.Range(min, max) => s"${fullNameDayOfTheWeek(min)} through ${fullNameDayOfTheWeek(max)}"
        case domain.CronExpr.RangeLabels(min, max) => s"${fullNameDayOfTheWeek(in.labels(min))} through ${fullNameDayOfTheWeek(in.labels(max))}"