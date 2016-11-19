package com.shuttleql.services.announcement

import org.scalatra._

class AnnouncementServiceServlet extends AnnouncementServiceStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

}
