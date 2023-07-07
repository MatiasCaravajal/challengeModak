package org.modak.notificationservice.ratelimiter;

public class NotificationException extends RuntimeException {

  public NotificationException(String message) {
    super(message);
  }
}
