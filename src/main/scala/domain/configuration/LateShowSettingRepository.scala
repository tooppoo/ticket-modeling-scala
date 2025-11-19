package org.philomagi.ticket_modeling
package domain.configuration

trait LateShowSettingRepository {
  def getSetting: Either[Exception, LateShowSetting]
}
