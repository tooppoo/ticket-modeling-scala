package org.philomagi.ticket_modeling
package domain.calendar

import org.scalatest.funsuite.AnyFunSuite
import java.time.{LocalDateTime, Month}

class CalendarTest extends AnyFunSuite {

  // テスト用の祝日データ: 2023年5月3日(水)憲法記念日, 5月4日(木)みどりの日, 5月5日(金)こどもの日
  private val holidays = Set(
    LocalDateTime.of(2023, Month.MAY, 3, 0, 0),
    LocalDateTime.of(2023, Month.MAY, 4, 0, 0),
    LocalDateTime.of(2023, Month.MAY, 5, 0, 0)
  )
  private val calendar = new Calendar(holidays)

  test("isRegularDay (平日判定): 祝日でない平日は True になる") {
    // 2023/5/1(月), 5/2(火) は平日
    assert(calendar.isRegularDay(LocalDateTime.of(2023, Month.MAY, 1, 10, 0)))
    assert(calendar.isRegularDay(LocalDateTime.of(2023, Month.MAY, 2, 20, 0)))
  }

  test("isRegularDay (平日判定): 土日は False になる") {
    // 2023/5/6(土), 5/7(日)
    assert(!calendar.isRegularDay(LocalDateTime.of(2023, Month.MAY, 6, 10, 0)))
    assert(!calendar.isRegularDay(LocalDateTime.of(2023, Month.MAY, 7, 10, 0)))
  }

  test("isRegularDay (平日判定): 平日であっても祝日は False になる") {
    // 2023/5/3(水) 憲法記念日
    assert(!calendar.isRegularDay(LocalDateTime.of(2023, Month.MAY, 3, 10, 0)))
  }

  test("isWeekEnd (土日判定): 土日は True になる") {
    assert(calendar.isWeekEnd(LocalDateTime.of(2023, Month.MAY, 6, 10, 0))) // 土
    assert(calendar.isWeekEnd(LocalDateTime.of(2023, Month.MAY, 7, 10, 0))) // 日
  }

  test("isWeekEnd (土日判定): 平日および祝日の平日は False になる") {
    assert(!calendar.isWeekEnd(LocalDateTime.of(2023, Month.MAY, 1, 10, 0))) // 月
    assert(!calendar.isWeekEnd(LocalDateTime.of(2023, Month.MAY, 3, 10, 0))) // 水(祝)
  }

  test("isHoliday (祝日判定): 設定された祝日は True になる (時刻は無視される)") {
    // 登録は 00:00 だが、上映時刻が 19:00 でも True
    assert(calendar.isHoliday(LocalDateTime.of(2023, Month.MAY, 3, 19, 0)))
  }

  test("isHoliday (祝日判定): 設定されていない日は False になる") {
    assert(!calendar.isHoliday(LocalDateTime.of(2023, Month.MAY, 1, 10, 0)))
  }

  test("isMovieDay (映画の日判定): 毎月1日は True になる") {
    // 1月1日だけでなく、5月1日も True
    assert(calendar.isMovieDay(LocalDateTime.of(2023, Month.MAY, 1, 10, 0)))
    assert(calendar.isMovieDay(LocalDateTime.of(2023, Month.DECEMBER, 1, 10, 0)))
  }

  test("isMovieDay (映画の日判定): 1日以外は False になる") {
    assert(!calendar.isMovieDay(LocalDateTime.of(2023, Month.JANUARY, 2, 10, 0)))
  }

  // matchDateType の優先順位テスト
  test("matchDateType: 映画の日 > 祝日 > 週末 > 平日の優先順位で判定される (映画の日が最優先)") {
    // 2023/5/3 は祝日(平日)なので Holiday
    val dt = LocalDateTime.of(2023, Month.MAY, 3, 10, 0)
    val t = calendar.matchDateType(dt)
    assert(t.isInstanceOf[Calendar.Holiday])
  }

  test("matchDateType: 土日は WeekEnd になる") {
    val sat = LocalDateTime.of(2023, Month.MAY, 6, 10, 0) // 土
    val sun = LocalDateTime.of(2023, Month.MAY, 7, 10, 0) // 日
    assert(calendar.matchDateType(sat).isInstanceOf[Calendar.WeekEnd])
    assert(calendar.matchDateType(sun).isInstanceOf[Calendar.WeekEnd])
  }

  test("matchDateType: 1日で祝日でも週末でもない日は MovieDay になる") {
    // 2023/5/1 は月曜、祝日リストにも含まれていない
    val dt = LocalDateTime.of(2023, Month.MAY, 1, 10, 0)
    val tt = calendar.matchDateType(dt)
    assert(tt.isInstanceOf[Calendar.MovieDay])
  }

  test("matchDateType: 祝日でも1日なら 映画の日(MovieDay) が最優先") {
    // 2023/5/1 を祝日に追加して優先度を検証 (この日は月曜で平日)。映画の日が最優先のため MovieDay が選ばれる
    val calendarWithMay1Holiday = new Calendar(holidays + LocalDateTime.of(2023, Month.MAY, 1, 0, 0))
    val dt = LocalDateTime.of(2023, Month.MAY, 1, 10, 0)
    val tt = calendarWithMay1Holiday.matchDateType(dt)
    assert(tt.isInstanceOf[Calendar.MovieDay])
  }

  test("matchDateType: 1日が土曜・日曜でも 映画の日(MovieDay) が最優先") {
    // 2023/4/1 は土曜。映画の日が最優先のため MovieDay が選ばれる
    val dt = LocalDateTime.of(2023, Month.APRIL, 1, 10, 0)
    val tt = calendar.matchDateType(dt)
    assert(tt.isInstanceOf[Calendar.MovieDay])
  }

  test("matchDateType: それ以外は RegularDay になる") {
    val dt = LocalDateTime.of(2023, Month.MAY, 2, 10, 0) // 平日・非祝日・1日でもない
    val tt = calendar.matchDateType(dt)
    assert(tt.isInstanceOf[Calendar.RegularDay])
  }
}
