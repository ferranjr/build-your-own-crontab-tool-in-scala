package utils

import domain.{CronTabExpression, CronTime}

import java.time.ZoneId
import java.time.format.{DateTimeFormatter, FormatStyle}

trait Showable[A]:
  extension (a: A) def show: String

object Showable:
  given Showable[CronTime] with
    extension (in: CronTime) def show: String = s"${in.units.mkString(" ")}"

  private def labelStr(label: String, width: Int): String =
    s"$label${" " * (width - label.length)}"

  private val columnSize: Int = 14

  private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
  
  given Showable[CronTabExpression] with
    extension (in: CronTabExpression) def show: String =
      s"""
         |${labelStr("minutes", columnSize)}${in.minute.show}
         |${labelStr("hours", columnSize)}${in.hour.show}
         |${labelStr("day of month", columnSize)}${in.dayOfTheMonth.show}
         |${labelStr("month", columnSize)}${in.month.show}
         |${labelStr("day of week", columnSize)}${in.dayOfTheWeek.show}
         |${labelStr("command", columnSize)}${in.command}
         |
         |${in.toNaturalLanguage}
         |
         |${in.nextInstances(5).map(_.atZone(ZoneId.systemDefault()).format(dateTimeFormatter)).mkString("\n")}
         |""".stripMargin
