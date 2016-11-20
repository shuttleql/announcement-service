package com.shuttleql.services.announcement

import com.gandalf.HMACAuth
import com.shuttleql.services.announcement.dao.AnnouncementDAO
import com.shuttleql.services.announcement.table.Announcement
import com.shuttleql.services.announcement.util.TypeUtil
import com.typesafe.config.ConfigFactory
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{BadRequest, InternalServerError, NoContent, Ok}
import org.scalatra.json.JacksonJsonSupport

class AnnouncementServiceServlet extends AnnouncementServiceStack with JacksonJsonSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  val conf = ConfigFactory.load()

  private def getRequest = enrichRequest(request)
  private def getResponce = enrichResponse(response)

  before() {
    auth
    contentType = formats("json")
  }

  after() {
  }

  def auth() {
    val token = getRequest.header("Authorization")
    val key = getRequest.header("Authorization-Key")
    val secret = conf.getString("secrets.hmac_secret")

    (token, key) match {
      case (Some(t), Some(k)) =>
        val split = t.split("HMAC ")
        split.length match {
          case 2 =>
            HMACAuth.validateHost(split(1), k, secret) match {
              case true => return
              case false =>
                halt(status=401, reason="Forbidden");
            }
          case _ =>
            halt(status=401, reason="Forbidden");
        }
      case _ =>
        halt(status=401, reason="Forbidden");
    }
  }

  get("/setup") {
    AnnouncementDAO.setupTables() match {
      case Some(result) => NoContent(reason = "Success")
      case None => InternalServerError(reason = "Error creating tables")
    }
  }

  get("/announcements/:offset/:size") {
    (TypeUtil.toInt(params.get("offset")), TypeUtil.toInt(params.get("size"))) match {
      case (Some(offset: Int), Some(size: Int)) =>
        AnnouncementDAO.getPage(offset, size) match {
          case Some(announcements) => Ok(announcements)
          case None => InternalServerError(reason = "Error fetching announcements")
        }
      case _ =>
        BadRequest(reason = "Problem with parsing parameters")
    }
  }

  post("/announcements") {
    try {
      val announcement = parsedBody.extract[Announcement]

      AnnouncementDAO.create(announcement) match {
        case Some(announce) => Ok(announce)
        case None => InternalServerError(reason = "Error creating announcement")
      }
    } catch {
      case e: Exception => BadRequest(reason = "Problem with payload")
    }
  }

}
