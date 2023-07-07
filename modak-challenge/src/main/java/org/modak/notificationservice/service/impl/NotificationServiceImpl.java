package org.modak.notificationservice.service.impl;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.modak.notificationservice.ratelimiter.NotificationException;
import org.modak.notificationservice.ratelimiter.NotificationRule;
import org.modak.notificationservice.ratelimiter.RateLimiter;
import org.modak.notificationservice.ratelimiter.TypeMessage;
import org.modak.notificationservice.service.NotificationService;
import org.springframework.util.Assert;

public class NotificationServiceImpl implements NotificationService {

  /**
   * The gateway, cannot be null
   */
  private final Gateway gateway;
  /**
   * The rateLimiter, cannot be null
   */
  private final RateLimiter rateLimiter;

  /**
   * Default constructor
   */
  public NotificationServiceImpl() {
    this.gateway = new Gateway();
    this.rateLimiter = new RateLimiter(buildNotificationRules());
  }

  /**
   * Send a message evaluating the {@link NotificationRule})
   * if any  rules are not met, it throws an {@link NotificationException})
   *
   * @param type    cannot be null or empty.
   * @param userId  cannot be null or empty
   * @param message cannot be null or empty
   *
   * @throws NotificationException if there are too many requests for that user and type.
   */
  @Override
  public void send(String type, String userId, String message) throws NotificationException {
    Assert.hasLength(type, "The type cannot be null or empty.");
    Assert.hasLength(userId, "The userId cannot be null or empty.");
    Assert.hasLength(message, "The message cannot be null or empty.");

    try {
      rateLimiter.sendMessage(userId, type, message, gateway);
    } catch (NotificationException ex) {
      System.out.println(ex.getMessage());
    }
  }

  public class Gateway {

    /* already implemented */
    public void send(String userId, String message) {
      System.out.println("sending message to user " + userId);
    }
  }
  /**
   * returns a list of default rules to be used by rate limiter.
   */
  public List<NotificationRule> buildNotificationRules() {
    List<NotificationRule> notificationRules = new ArrayList<>();
    notificationRules.add(new NotificationRule(TypeMessage.STATUS, 2, ChronoUnit.MINUTES));
    notificationRules.add(new NotificationRule(TypeMessage.NEWS, 1, ChronoUnit.DAYS));
    notificationRules.add(new NotificationRule(TypeMessage.MARKETING, 2, ChronoUnit.HOURS));
    notificationRules.add(new NotificationRule(TypeMessage.UPDATE, 2, ChronoUnit.WEEKS));
    return notificationRules;
  }
}