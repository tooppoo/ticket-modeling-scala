package org.philomagi.ticket_modeling
package application.service

import application.PriceDefinition.*
import domain.calendar.{Calendar, CalendarRepository}
import domain.configuration.{LateShowSetting, LateShowSettingRepository}
import domain.pricing.Price
import domain.theater.Theater
import domain.ticket.{Ticket, TicketRepository}
import utils.TaggedType

import org.scalatest.funsuite.AnyFunSuite

import java.time.LocalDateTime

class CalculateTicketPriceTest extends AnyFunSuite {

  // ----- Test Doubles -----
  private class StubTicketRepository extends TicketRepository {
    override def find(theaterId: Theater.Id, movieStartAt: LocalDateTime): Either[Exception, Ticket] = {
      val ticket = Ticket(
        TaggedType[Ticket.Id, "TicketId"]("t-1"),
        theaterId,
        movieStartAt,
        Seq.empty
      )
      Right(ticket)
    }
  }

  private class StubLateShowSettingRepository(setting: LateShowSetting) extends LateShowSettingRepository {
    override def getSetting = Right(setting)
  }

  private class StubCalendarRepository(holidays: Set[LocalDateTime]) extends CalendarRepository {
    override def getCalendar(begin: LocalDateTime, end: LocalDateTime) = Right(new Calendar(holidays))
  }

  private val theaterId: Theater.Id = TaggedType[Theater.Id, "TheaterId"]("theater-1")

  // Default late show: >= 20:00
  private val defaultLateShow: LateShowSetting = LateShowSetting(
    TaggedType[scala.Int, "LateShowStartHour"](20),
    TaggedType[scala.Int, "LateShowStartMinute"](0),
    LateShowSetting.Condition.parse(">=").getOrElse(fail("condition parse failed"))
  )

  private def service(holidays: Set[LocalDateTime] = Set.empty, late: LateShowSetting = defaultLateShow) =
    new CalculateTicketPrice(
      new StubTicketRepository,
      new StubLateShowSettingRepository(late),
      new StubCalendarRepository(holidays)
    )

  private def onWeekday(hour: Int, minute: Int = 0): LocalDateTime = {
    // Pick a Monday that is not the 1st to avoid MovieDay interference: 2023-05-22 (Mon)
    LocalDateTime.of(2023, 5, 22, hour, minute)
  }

  private def onWeekend(hour: Int, minute: Int = 0): LocalDateTime = {
    // Saturday: 2023-05-27
    LocalDateTime.of(2023, 5, 27, hour, minute)
  }

  private def onHoliday(hour: Int, minute: Int = 0): (LocalDateTime, Set[LocalDateTime]) = {
    // Use 2023-05-23 as a holiday (Tuesday) to avoid weekend/movie day effects
    val dt = LocalDateTime.of(2023, 5, 23, hour, minute)
    (dt, Set(dt))
  }

  private def onMovieDayWeekday(hour: Int, minute: Int = 0): LocalDateTime = {
    // 2023-08-01 is a Tuesday
    LocalDateTime.of(2023, 8, 1, hour, minute)
  }

  private def onMovieDayWeekend(hour: Int, minute: Int = 0): LocalDateTime = {
    // 2023-07-01 is a Saturday
    LocalDateTime.of(2023, 7, 1, hour, minute)
  }

  // ----- CinemaCitizen -----
  test("CinemaCitizen: Weekday - before 20:00 => 1000") {
    val svc = service()
    val result = svc.run(CinemaCitizen, onWeekday(19), theaterId)
    assert(result == Right(Price(1000)))
  }

  test("CinemaCitizen: Weekend - before 20:00 => 1300") {
    val svc = service()
    val result = svc.run(CinemaCitizen, onWeekend(19), theaterId)
    assert(result == Right(Price(1300)))
  }

  test("CinemaCitizen: Weekend - 20:00 or later (late) => 1000") {
    val svc = service()
    val result = svc.run(CinemaCitizen, onWeekend(20), theaterId)
    assert(result == Right(Price(1000)))
  }

  test("CinemaCitizen: Holiday - before 20:00 => 1300; late => 1000") {
    val (holiday19, holidays) = onHoliday(19)
    val svc = service(holidays)
    assert(svc.run(CinemaCitizen, holiday19, theaterId) == Right(Price(1300)))

    val (holiday20, holidays2) = onHoliday(20)
    val svc2 = service(holidays2)
    assert(svc2.run(CinemaCitizen, holiday20, theaterId) == Right(Price(1000)))
  }

  test("CinemaCitizen: Movie day weekday => 1000 (movie day note)") {
    val svc = service()
    val result = svc.run(CinemaCitizen, onMovieDayWeekday(10), theaterId)
    assert(result == Right(Price(1000)))
  }

  test("CinemaCitizen: Movie day weekend - before 20:00 => 1300; late => 1000") {
    val svc = service()
    assert(svc.run(CinemaCitizen, onMovieDayWeekend(19), theaterId) == Right(Price(1300)))
    assert(svc.run(CinemaCitizen, onMovieDayWeekend(20), theaterId) == Right(Price(1000)))
  }

  // ----- CinemaCitizenSenior -----
  test("CinemaCitizenSenior: always 1000") {
    val svc = service()
    val times = Seq(onWeekday(10), onWeekday(21), onWeekend(10), onWeekend(21), onMovieDayWeekday(12))
    times.foreach { dt => assert(svc.run(CinemaCitizenSenior, dt, theaterId) == Right(Price(1000))) }
  }

  // ----- Regular -----
  test("Regular: weekday before 20:00 => 2000; late => 1500") {
    val svc = service()
    assert(svc.run(Regular, onWeekday(19), theaterId) == Right(Price(2000)))
    assert(svc.run(Regular, onWeekday(20), theaterId) == Right(Price(1500)))
  }

  test("Regular: weekend before 20:00 => 2000; late => 1500") {
    val svc = service()
    assert(svc.run(Regular, onWeekend(19), theaterId) == Right(Price(2000)))
    assert(svc.run(Regular, onWeekend(20), theaterId) == Right(Price(1500)))
  }

  test("Regular: holiday before 20:00 => 2000; late => 1500") {
    val (h19, holidays) = onHoliday(19)
    val svc = service(holidays)
    assert(svc.run(Regular, h19, theaterId) == Right(Price(2000)))
    val h20 = h19.withHour(20)
    assert(svc.run(Regular, h20, theaterId) == Right(Price(1500)))
  }

  test("Regular: movie day => 1300") {
    val svc = service()
    val times = Seq(onMovieDayWeekday(10), onMovieDayWeekend(10), onMovieDayWeekend(21))
    times.foreach { dt => assert(svc.run(Regular, dt, theaterId) == Right(Price(1300))) }
  }

  // ----- Senior70Plus -----
  test("Senior70Plus: flat 1300") {
    val svc = service()
    assert(svc.run(Senior70Plus, onWeekday(10), theaterId) == Right(Price(1300)))
  }

  // ----- CollegeStudent -----
  test("CollegeStudent: regular days 1500; movie day 1300") {
    val svc = service()
    assert(svc.run(CollegeStudent, onWeekday(10), theaterId) == Right(Price(1500)))
    assert(svc.run(CollegeStudent, onWeekend(10), theaterId) == Right(Price(1500)))
    assert(svc.run(CollegeStudent, onMovieDayWeekday(10), theaterId) == Right(Price(1300)))
  }

  // ----- MiddleHighStudent -----
  test("MiddleHighStudent: flat 1000") {
    val svc = service()
    assert(svc.run(MiddleHighStudent, onWeekday(10), theaterId) == Right(Price(1000)))
  }

  // ----- ChildOrElementary -----
  test("ChildOrElementary: flat 1000") {
    val svc = service()
    assert(svc.run(ChildOrElementary, onWeekday(10), theaterId) == Right(Price(1000)))
  }

  // ----- DisabledStudentOrAbove and Companion -----
  test("DisabledStudentOrAbove: flat 1000; CompanionOfDisabledStudentOrAbove: flat 1000") {
    val svc = service()
    assert(svc.run(DisabledStudentOrAbove, onWeekday(10), theaterId) == Right(Price(1000)))
    assert(svc.run(CompanionOfDisabledStudentOrAbove, onWeekday(10), theaterId) == Right(Price(1000)))
  }

  // ----- DisabledUnderHighSchool and Companion -----
  test("DisabledUnderHighSchool: flat 900; CompanionOfDisabledUnderHighSchool: flat 900") {
    val svc = service()
    assert(svc.run(DisabledUnderHighSchool, onWeekday(10), theaterId) == Right(Price(900)))
    assert(svc.run(CompanionOfDisabledUnderHighSchool, onWeekday(10), theaterId) == Right(Price(900)))
  }
}
