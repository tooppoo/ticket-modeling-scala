package org.philomagi.ticket_modeling
package domain.calendar

import java.time.{DayOfWeek, LocalDateTime}

class Calendar(
                private val holidays: Set[LocalDateTime]
              ) {
  def isRegularDay(dt: LocalDateTime): Boolean = {
    !isHoliday(dt)
    && dt.getDayOfWeek != DayOfWeek.SATURDAY
    && dt.getDayOfWeek != DayOfWeek.SUNDAY
  }

  def isWeekEnd(dt: LocalDateTime): Boolean =
    dt.getDayOfWeek == DayOfWeek.SATURDAY
    || dt.getDayOfWeek == DayOfWeek.SUNDAY

  def isMovieDay(dt: LocalDateTime): Boolean = dt.getDayOfMonth == 1

  def isHoliday(dt: LocalDateTime): Boolean = holidays.exists(h => h.isSameDate(dt))
  
  extension(dt: LocalDateTime) {
    private def isSameDate(other: LocalDateTime): Boolean = dt.toLocalDate == other.toLocalDate
  }
}
