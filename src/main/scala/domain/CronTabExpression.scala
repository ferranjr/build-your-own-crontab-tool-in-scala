package domain

import utils.NaturalLanguage.given

import java.time.LocalDateTime
import scala.util.Try

case class CronTabExpression(
  minute: Minute,
  hour: Hour,
  dayOfTheMonth: DayOfTheMonth,
  month: Month,
  dayOfTheWeek: DayOfTheWeek,
  command: String
) {

  def toNaturalLanguage: String =
    s"${minute.naturalLanguage}, ${hour.naturalLanguage}, ${dayOfTheWeek.naturalLanguage}"

  private def convertDayOfTheWeekCron(dayOfTheWeek: java.time.DayOfWeek): Int =
    dayOfTheWeek match {
      case java.time.DayOfWeek.SUNDAY     => 0
      case other                          => other.getValue
    }

  private def nextInstance(seed: LocalDateTime): LocalDateTime = {
    val seedMinute = seed.getMinute
    val minute = this.minute.units.dropWhile(_ < seedMinute)
      .headOption.getOrElse(this.minute.units.head)
    
    val seedHour = if seedMinute > minute then seed.getHour + 1 else seed.getHour
    val hour = this.hour.units.dropWhile(_ < seedHour)
      .headOption.getOrElse(this.hour.units.head)
    
    val seedDayOfTheMonth = if seedHour > hour then seed.getDayOfMonth + 1 else seed.getDayOfMonth
    val dayOfTheMonth = this.dayOfTheMonth.units.dropWhile(_ < seedDayOfTheMonth)
      .headOption.getOrElse(this.dayOfTheMonth.units.head)
    
    val seedMonth = if seedDayOfTheMonth > dayOfTheMonth then seed.getMonth.getValue + 1 else seed.getMonth.getValue
    val month = this.month.units.dropWhile(_ < seedMonth)
      .headOption.getOrElse(this.month.units.head)
    
    val year = if seedMonth > month then seed.getYear + 1 else seed.getYear
    
    Try(
      LocalDateTime.of(
        year,
        month,
        dayOfTheMonth,
        hour,
        minute,
        0
      )
    ).getOrElse(nextInstance(seed.plusDays(1)))
  }

  def nextInstances(
    n: Int,
    seed: LocalDateTime = LocalDateTime.now()
  ): Seq[LocalDateTime] = {
    LazyList.from(0)
      .scanLeft(nextInstance(seed)) { case (prev, _) =>
        nextInstance(prev.plusMinutes(1))
      }
      .filter { candidate =>
        val value = convertDayOfTheWeekCron(candidate.getDayOfWeek)
        this.dayOfTheWeek.units.contains(value)
      }
      .take(n)
      .toList
  }
}


