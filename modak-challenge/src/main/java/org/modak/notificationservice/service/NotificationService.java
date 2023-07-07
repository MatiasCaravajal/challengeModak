package org.modak.notificationservice.service;

public interface NotificationService {
  void send(String type, String userId, String message);
}
