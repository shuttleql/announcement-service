package com.shuttleql.services.announcement.dao

import com.shuttleql.services.announcement.table.{Announcement, Announcements}
import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AnnouncementDAO extends TableQuery(new Announcements(_)) {
  def initDb() = {
    Database.forConfig("db")
  }

  def setupTables(): Option[Unit] = {
    val db = initDb()

    try {
      Option(Await.result(db.run(this.schema.create), Duration.Inf))
    } catch {
      case e: Exception => None
    } finally {
      db.close()
    }
  }

  def create(announcement: Announcement): Option[Announcement] = {
    val db = initDb()

    try {
      val result: Announcement = Await.result(db.run(this returning this += announcement), Duration.Inf)
      Option(result)
    } catch {
      case e: Exception =>
        println(e)
        None
    } finally {
      db.close()
    }
  }

  def getPage(offset: Int, size: Int): Option[Seq[Announcement]] = {
    val db = initDb()

    try {
      val result: Seq[Announcement] = Await.result(db.run(
        this.sortBy(_.ctime.desc)
          .drop(offset)
          .take(size)
          .result), Duration.Inf)
      Option(result)
    } catch {
      case e: Exception => None
    } finally {
      db.close()
    }
  }

}
