package org.philomagi.ticket_modeling
package domain.pricing

trait PricingRule {
  def calculate(input: PricingInput): Either[Exception, Price]
}
