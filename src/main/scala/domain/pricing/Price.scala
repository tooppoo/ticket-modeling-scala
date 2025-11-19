package org.philomagi.ticket_modeling
package domain.pricing

case class Price(value: Int) {
  assert(value >= 0)
}
