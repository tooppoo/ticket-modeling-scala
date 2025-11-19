package org.philomagi.ticket_modeling
package domain.calendar

import java.time.LocalDateTime

trait CalendarRepository {
  def getCalendar(begin: LocalDateTime, end: LocalDateTime): Either[Exception, Calendar]
}
