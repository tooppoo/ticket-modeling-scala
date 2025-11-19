package org.philomagi.ticket_modeling
package domain.ticket

import domain.theater.Theater

import java.time.LocalDateTime

trait TicketRepository {
  def find(theaterId: Theater.Id, movieStartAt: LocalDateTime): Either[Exception, Ticket]
}
