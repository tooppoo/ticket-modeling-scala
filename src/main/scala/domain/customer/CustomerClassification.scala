package org.philomagi.ticket_modeling
package domain.customer

trait CustomerClassification {
  import utils.TaggedType._

  def id: String @@ "CustomerClassificationId"
}
