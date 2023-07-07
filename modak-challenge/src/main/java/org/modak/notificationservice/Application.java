package org.modak.notificationservice;

import org.modak.notificationservice.service.impl.NotificationServiceImpl;

public class Application {

  public static void main(String[] args) {

    NotificationServiceImpl service = new NotificationServiceImpl();

    service.send("news", "user", "news 1");

    service.send("news", "user", "news 2");

    service.send("news", "user", "news 3");

    service.send("news", "another user", "news 1");

    service.send("news", "user", "update 1");

  }
}
