package org.philomagi.ticket_modeling
package utils

object TaggedType {
  import scala.language.implicitConversions
  type @@[T, U] = T

  inline def apply[R, T](r: R): R @@ T = r
  inline implicit def unwrap[R, T](t: R @@ T): R = t
}
