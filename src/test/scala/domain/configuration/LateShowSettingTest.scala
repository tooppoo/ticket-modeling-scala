package org.philomagi.ticket_modeling
package domain.configuration

import domain.configuration.LateShowSetting.Condition
import utils.TaggedType

import org.scalatest.funsuite.AnyFunSuite
import java.time.LocalDateTime

class LateShowSettingTest extends AnyFunSuite {

  test("Condition.parse: '>=' をパースできる") {
    val result = Condition.parse(">=")
    assert(result.isRight)
  }

  test("Condition.parse: '>' をパースできる") {
    val result = Condition.parse(">")
    assert(result.isRight)
  }

  test("Condition.parse: 不正な文字列はエラーになる") {
    val result = Condition.parse("invalid")
    assert(result.isLeft)
    assert(result.left.getOrElse(new Exception).isInstanceOf[Condition.ParseConditionException])
  }

  test("isLateShow (>=): 20:00以上の場合") {
    // 20:00
    val condition = Condition.parse(">=").getOrElse(fail("parse failed"))
    val setting = LateShowSetting(
      TaggedType(20),
      TaggedType(0),
      condition
    )

    // 19:59 -> false
    assert(!setting.isLateShow(LocalDateTime.of(2023, 1, 1, 19, 59)))
    // 20:00 -> true
    assert(setting.isLateShow(LocalDateTime.of(2023, 1, 1, 20, 0)))
    // 20:01 -> true
    assert(setting.isLateShow(LocalDateTime.of(2023, 1, 1, 20, 1)))
  }

  test("isLateShow (>): 20:00より後の場合") {
    val condition = Condition.parse(">").getOrElse(fail("parse failed"))
    val setting = LateShowSetting(
      TaggedType(20),
      TaggedType(0),
      condition
    )

    // 19:59 -> false
    assert(!setting.isLateShow(LocalDateTime.of(2023, 1, 1, 19, 59)))
    // 20:00 -> false (より大きい、なので境界は含まない)
    assert(!setting.isLateShow(LocalDateTime.of(2023, 1, 1, 20, 0)))
    // 20:01 -> true
    assert(setting.isLateShow(LocalDateTime.of(2023, 1, 1, 20, 1)))
  }
  
  test("isLateShow (>=): 分単位の判定確認 (21:30設定)") {
    val condition = Condition.parse(">=").getOrElse(fail("parse failed"))
    val setting = LateShowSetting(
      TaggedType(21),
      TaggedType(30),
      condition
    )

    // 21:29 -> false
    assert(!setting.isLateShow(LocalDateTime.of(2023, 1, 1, 21, 29)))
    // 21:30 -> true
    assert(setting.isLateShow(LocalDateTime.of(2023, 1, 1, 21, 30)))
    // 22:00 -> true (時間が大きければ分は関係ない)
    assert(setting.isLateShow(LocalDateTime.of(2023, 1, 1, 22, 0)))
  }

  test("isLateShow (>): 分単位の判定確認 (21:30設定)") {
    val condition = Condition.parse(">").getOrElse(fail("parse failed"))
    val setting = LateShowSetting(
      TaggedType(21),
      TaggedType(30),
      condition
    )

    // 21:29 -> false
    assert(!setting.isLateShow(LocalDateTime.of(2023, 1, 1, 21, 29)))
    // 21:30 -> false
    assert(!setting.isLateShow(LocalDateTime.of(2023, 1, 1, 21, 30)))
    // 21:31 -> true
    assert(setting.isLateShow(LocalDateTime.of(2023, 1, 1, 21, 31)))
    // 22:00 -> true (時間が大きければ分は関係ない)
    assert(setting.isLateShow(LocalDateTime.of(2023, 1, 1, 22, 0)))
  }
}
