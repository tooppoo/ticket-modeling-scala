package org.philomagi.ticket_modeling
package domain.pricing

import domain.calendar.Calendar
import domain.configuration.LateShowSetting
import domain.customer.CustomerClassification
import domain.ticket.Ticket

case class PricingInput(
                       customerClassification: CustomerClassification,
                       ticket: Ticket,
                       calendar: Calendar,
                       lateShowSetting: LateShowSetting
                       )
