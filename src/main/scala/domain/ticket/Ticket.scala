package org.philomagi.ticket_modeling
package domain.ticket

import domain.theater.Theater

import java.time.LocalDateTime

class Ticket(
              val id: Ticket.Id,
              val theaterId: Theater.Id,
              val movieStartAt: LocalDateTime,
              val screeningAttributes: Seq[Ticket.ScreeningAttribute]
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
