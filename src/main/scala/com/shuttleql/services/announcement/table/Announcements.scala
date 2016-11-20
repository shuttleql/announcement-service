package com.shuttleql.services.announcement.table

import java.sql.Timestamp

import slick.driver.PostgresDriver.api._
import slick.profile.SqlProfile.ColumnOption.SqlType

case class Announcement(
  id: Option[Int] = None,
  message: String,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis())
)

class Announcements(tag: Tag) extends Table[Announcement](tag, "announcements") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def message = column[String]("message")
  def ctime = column[Timestamp]("ctime", SqlType("timestamp not null default CURRENT_TIMESTAMP"))
  def * = (id.?, message, ctime) <> (Announcement.tupled, Announcement.unapply)

  def idxCtime= index("idx_ctime", ctime)
}
