package org.philomagi.ticket_modeling
package domain.ticket

import domain.theater.Theater

import java.time.LocalDateTime

class Ticket(
              private val id: Ticket.Id,
              private val theaterId: Theater.Id,
              private val movieStartAt: LocalDateTime,
              private val screeningAttributes: Seq[Ticket.ScreeningAttribute]
            )
object Ticket {
  import utils.TaggedType.*

  def apply(
             id: Ticket.Id,
             theaterId: Theater.Id,
             movieStartAt: LocalDateTime,
             screeningAttributes: Seq[Ticket.ScreeningAttribute]
           ) = new Ticket(id, theaterId, movieStartAt, screeningAttributes)

  type Id = String @@ "TicketId"

  trait ScreeningAttribute {
    val id: String @@ "ScreeningAttributeId"
  }
}
