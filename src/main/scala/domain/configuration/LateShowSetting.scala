package org.philomagi.ticket_modeling
package domain.configuration

import utils.TaggedType.@@

import java.time.LocalDateTime

case class LateShowSetting(
                            startHour: Int @@ "LateShowStartHour",
                            startMinute: Int @@ "LateShowStartMinute",
                            condition: LateShowSetting.Condition
                          ) {
  def isLateShow(dt: LocalDateTime): Boolean = condition(dt, this)
}

object LateShowSetting {
  trait Condition {
    def apply(dt: LocalDateTime, setting: LateShowSetting): Boolean
  }
  object Condition {
    def parse(str: String): Either[Exception, Condition] = str match {
      case ">=" => Right(IsLaterOrEqual)
      case ">" => Right(IsLaterThan)
      case _ => Left(new ParseConditionException(s"Invalid condition: $str"))
    }

    private case object IsLaterOrEqual extends Condition {
      override def apply(dt: LocalDateTime, setting: LateShowSetting): Boolean =
        dt.getHour > setting.startHour || (dt.getHour == setting.startHour && dt.getMinute >= setting.startMinute)
    }
    private case object IsLaterThan extends Condition {
      override def apply(dt: LocalDateTime, setting: LateShowSetting): Boolean =
        dt.getHour > setting.startHour || (dt.getHour == setting.startHour && dt.getMinute > setting.startMinute)
    }

    class ParseConditionException(msg: String) extends Exception(msg)
  }
}
