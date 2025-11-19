package org.philomagi.ticket_modeling
package application.service

import domain.calendar.CalendarRepository
import domain.configuration.LateShowSettingRepository
import domain.customer.CustomerClassification
import domain.theater.Theater
import domain.ticket.TicketRepository

import org.philomagi.ticket_modeling.application.PriceDefinition
import org.philomagi.ticket_modeling.domain.pricing.{Price, PricingInput}

import java.time.LocalDateTime

class CalculateTicketPrice(
                          private val ticketRepository: TicketRepository,
                          private val lateShowSettingRepository: LateShowSettingRepository,
                          private val calendarRepository: CalendarRepository,
                          ) {
  def run(
    classification: CustomerClassification,
    movieStartAt: LocalDateTime,
    theaterId: Theater.Id
  ): Either[Exception, Price] = {
    val result = for {
      ticket <- ticketRepository.find(theaterId, movieStartAt)
      lateShowSetting <- lateShowSettingRepository.getSetting
      calendar <- calendarRepository.getCalendar(movieStartAt.minusDays(1), movieStartAt.plusDays(1))
    } yield {
      val pricingRule = PriceDefinition.matchPricingRule(classification)
      val pricingInput = PricingInput(classification, ticket, calendar, lateShowSetting)

      pricingRule.calculate(pricingInput)
    }

    result.fold(e => Left(e), r => r)
  }
}
