package org.philomagi.ticket_modeling
package application

import domain.calendar.Calendar
import domain.customer.CustomerClassification
import domain.pricing.{Price, PricingInput, PricingRule}
import utils.TaggedType
import utils.TaggedType.@@

object PriceDefinition {
  // シネマシティ チケット料金の区分定義
  // 出典: docs/CinemaCityTicketPricing.md

  def matchPricingRule(classification: CustomerClassification): PricingRule = classification match {
    case CinemaCitizen => CinemaCitizenPricingRule
    case CinemaCitizenSenior => CinemaCitizenSeniorPricingRule
    case Regular => RegularPricingRule
    case Senior70Plus => Senior70PlusPricingRule
    case CollegeStudent => CollegeStudentPricingRule
    case MiddleHighStudent => MiddleHighStudentPricingRule
    case ChildOrElementary => ChildOrElementaryPricingRule
    case DisabledStudentOrAbove => DisabledStudentOrAbovePricingRule
    case CompanionOfDisabledStudentOrAbove => CompanionOfDisabledStudentOrAbovePricingRule
  }

  // シネマシティズン
  case object CinemaCitizen extends CustomerClassification {
    val id: String @@ "CustomerClassificationId" = TaggedType("CinemaCitizen")
  }
  case object CinemaCitizenPricingRule extends PricingRule {
    override def calculate(input: PricingInput): Either[Exception, Price] = input.customerClassification match {
      case CinemaCitizen => input.calendar.matchDateType(input.ticket.movieStartAt) match {
        case Calendar.RegularDay(_) => Right(Price(1000))

        case Calendar.WeekEnd(dateTime) if input.lateShowSetting.isLateShow(dateTime) => Right(Price(1000))
        case Calendar.WeekEnd(dateTime) => Right(Price(1300))

        case Calendar.Holiday(dateTime) if input.lateShowSetting.isLateShow(dateTime) => Right(Price(1000))
        case Calendar.Holiday(dateTime) => Right(Price(1300))

        case Calendar.MovieDay(dateTime) if input.calendar.isRegularDay(input.ticket.movieStartAt) => Right(Price(1000))
        case Calendar.MovieDay(dateTime) => if (
          (
            input.calendar.isWeekEnd(input.ticket.movieStartAt)
            || input.calendar.isHoliday(input.ticket.movieStartAt)
          )
          && input.lateShowSetting.isLateShow(dateTime)
        ) {
          Right(Price(1000))
        }
        else {
          Right(Price(1300))
        }
      }
      case _ => Left(new InvalidCustomerClassificationException(input.customerClassification))
    }
  }

  // シネマシティズン（60才以上）
  case object CinemaCitizenSenior extends CustomerClassification {
    val id: String @@ "CustomerClassificationId" = TaggedType("CinemaCitizenSenior")
  }
  case object CinemaCitizenSeniorPricingRule extends PricingRule {
    override def calculate(input: PricingInput): Either[Exception, Price] = input.customerClassification match {
      case CinemaCitizenSenior => Right(Price(1000))
      case _ => Left(new InvalidCustomerClassificationException(input.customerClassification))
    }
  }

  // 一般
  case object Regular extends CustomerClassification {
    val id: String @@ "CustomerClassificationId" = TaggedType("Regular")
  }
  case object RegularPricingRule extends PricingRule {
    override def calculate(input: PricingInput): Either[Exception, Price] = input.customerClassification match {
      case Regular => input.calendar.matchDateType(input.ticket.movieStartAt) match {
        case Calendar.RegularDay(dateTime) if input.lateShowSetting.isLateShow(dateTime) => Right(Price(1500))
        case Calendar.RegularDay(dateTime) => Right(Price(2000))

        case Calendar.WeekEnd(dateTime) if input.lateShowSetting.isLateShow(dateTime) => Right(Price(1500))
        case Calendar.WeekEnd(dateTime) => Right(Price(2000))

        case Calendar.Holiday(dateTime) if input.lateShowSetting.isLateShow(dateTime) => Right(Price(1500))
        case Calendar.Holiday(dateTime) => Right(Price(2000))

        case Calendar.MovieDay(dateTime) => Right(Price(1300))
      }
      case _ => Left(new InvalidCustomerClassificationException(input.customerClassification))
    }
  }

  // シニア（70才以上）
  case object Senior70Plus extends CustomerClassification {
    val id: String @@ "CustomerClassificationId" = TaggedType("Senior70Plus")
  }
  case object Senior70PlusPricingRule extends PricingRule {
    override def calculate(input: PricingInput): Either[Exception, Price] = input.customerClassification match {
      case Senior70Plus => Right(Price(1300))
      case _ => Left(new InvalidCustomerClassificationException(input.customerClassification))
    }
  }

  // 学生（大・専）
  case object CollegeStudent extends CustomerClassification {
    val id: String @@ "CustomerClassificationId" = TaggedType("CollegeStudent")
  }
  case object CollegeStudentPricingRule extends PricingRule {
    override def calculate(input: PricingInput): Either[Exception, Price] = input.customerClassification match {
      case CollegeStudent => input.calendar.matchDateType(input.ticket.movieStartAt) match {
        case Calendar.MovieDay(_) => Right(Price(1300))
        case _ => Right(Price(1500))
      }
      case _ => Left(new InvalidCustomerClassificationException(input.customerClassification))
    }
  }

  // 中・高校生
  case object MiddleHighStudent extends CustomerClassification {
    val id: String @@ "CustomerClassificationId" = TaggedType("MiddleHighStudent")
  }
  case object MiddleHighStudentPricingRule extends PricingRule {
    override def calculate(input: PricingInput): Either[Exception, Price] = input.customerClassification match {
      case MiddleHighStudent => Right(Price(1000))
      case _ => Left(new InvalidCustomerClassificationException(input.customerClassification))
    }
  }

  // 幼児（3才以上）・小学生
  case object ChildOrElementary extends CustomerClassification {
    val id: String @@ "CustomerClassificationId" = TaggedType("ChildOrElementary")
  }
  case object ChildOrElementaryPricingRule extends PricingRule {
    override def calculate(input: PricingInput): Either[Exception, Price] = input.customerClassification match {
      case ChildOrElementary => Right(Price(1000))
      case _ => Left(new InvalidCustomerClassificationException(input.customerClassification))
    }
  }

  // 障がい者（学生以上）
  case object DisabledStudentOrAbove extends CustomerClassification {
    val id: String @@ "CustomerClassificationId" = TaggedType("DisabledStudentOrAbove")
  }
  case object DisabledStudentOrAbovePricingRule extends PricingRule {
    override def calculate(input: PricingInput): Either[Exception, Price] = input.customerClassification match {
      case DisabledStudentOrAbove => Right(Price(1000))
      case _ => Left(new InvalidCustomerClassificationException(input.customerClassification))
    }
  }
  // 障がい者（学生以上）の同伴者
  case object CompanionOfDisabledStudentOrAbove extends CustomerClassification {
    val id: String @@ "CustomerClassificationId" = TaggedType("CompanionOfDisabledStudentOrAbove")
  }
  case object CompanionOfDisabledStudentOrAbovePricingRule extends PricingRule {
    override def calculate(input: PricingInput): Either[Exception, Price] = input.customerClassification match {
      case CompanionOfDisabledStudentOrAbove => Right(Price(1000))
      case _ => Left(new InvalidCustomerClassificationException(input.customerClassification))
    }
  }

  // 障がい者（高校以下）
  case object DisabledUnderHighSchool extends CustomerClassification {
    val id: String @@ "CustomerClassificationId" = TaggedType("DisabledHighOrBelow")
  }
  case object DisabledUnderHighSchoolPricingRule extends PricingRule {
    override def calculate(input: PricingInput): Either[Exception, Price] = input.customerClassification match {
      case DisabledUnderHighSchool => Right(Price(900))
      case _ => Left(new InvalidCustomerClassificationException(input.customerClassification))
    }
  }
  // 障がい者（高校以下）の同伴者
  case object CompanionOfDisabledUnderHighSchool extends CustomerClassification {
    val id: String @@ "CustomerClassificationId" = TaggedType("CompanionOfDisabledHighOrBelow")
  }
  case object CompanionOfDisabledUnderHighSchoolPricingRule extends PricingRule {
    override def calculate(input: PricingInput): Either[Exception, Price] = input.customerClassification match {
      case CompanionOfDisabledUnderHighSchool => Right(Price(900))
      case _ => Left(new InvalidCustomerClassificationException(input.customerClassification))
    }
  }

  class InvalidCustomerClassificationException(customerClassification: CustomerClassification)
    extends Exception(s"Invalid customer classification: ${customerClassification.id}")
}
