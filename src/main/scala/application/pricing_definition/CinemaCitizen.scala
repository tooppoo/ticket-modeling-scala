package org.philomagi.ticket_modeling
package application.pricing_definition

import domain.customer.CustomerClassification
import domain.pricing.{Price, PricingInput, PricingRule}

object CinemaCitizen {
  case object CinemaCitizenClassification extends CustomerClassification {
    override def id: String = "CinemaCitizen"
  }
  object CinemaCitizenPricingRule extends PricingRule {
    def calculate(input: PricingInput): Either[Exception, Price] = input.customerClassification match {
      case CinemaCitizenClassification => Right(Price(10))
      case _ => Left(new Exception("Not a CinemaCitizen"))
    }
  }
}
