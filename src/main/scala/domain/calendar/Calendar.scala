package org.philomagi.ticket_modeling
package domain.calendar

import java.time.{DayOfWeek, LocalDateTime}

class Calendar(
                private val holidays: Set[LocalDateTime]
              ) {
  def matchDateType(dt: LocalDateTime): Calendar.DateType = {
    // 優先順位: 映画の日 > 祝日 > 週末 > 平日
    if (isMovieDay(dt)) Calendar.MovieDay(dt)
    else if (isHoliday(dt)) Calendar.Holiday(dt)
    else if (isWeekEnd(dt)) Calendar.WeekEnd(dt)
    else Calendar.RegularDay(dt)
  }

  def isRegularDay(dt: LocalDateTime): Boolean = !isHoliday(dt) && !isWeekEnd(dt)

  def isWeekEnd(dt: LocalDateTime): Boolean =
    dt.getDayOfWeek == DayOfWeek.SATURDAY
    || dt.getDayOfWeek == DayOfWeek.SUNDAY

  def isMovieDay(dt: LocalDateTime): Boolean = dt.getDayOfMonth == 1

  def isHoliday(dt: LocalDateTime): Boolean = holidays.exists(h => h.isSameDate(dt))
  
  extension(dt: LocalDateTime) {
    private def isSameDate(other: LocalDateTime): Boolean = dt.toLocalDate == other.toLocalDate
  }
}
object Calendar {
  trait DateType {
    val dateTime: LocalDateTime
  }

  case class RegularDay(dateTime: LocalDateTime) extends DateType
  case class WeekEnd(dateTime: LocalDateTime) extends DateType
  case class MovieDay(dateTime: LocalDateTime) extends DateType
  case class Holiday(dateTime: LocalDateTime) extends DateType
}
