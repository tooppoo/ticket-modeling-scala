package org.philomagi.ticket_modeling
package application.service

import application.PriceDefinition._

object ListCustomerClassifications {
  def run = Seq(
    CinemaCitizen,
    CinemaCitizenSenior,
    Regular,
    Senior70Plus,
    CollegeStudent,
    MiddleHighStudent,
    ChildOrElementary,
    DisabledStudentOrAbove,
    CompanionOfDisabledStudentOrAbove,
    DisabledUnderHighSchool,
    CompanionOfDisabledUnderHighSchool,
  )
}
