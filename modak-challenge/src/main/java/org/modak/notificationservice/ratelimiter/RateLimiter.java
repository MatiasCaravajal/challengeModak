package org.modak.notificationservice.ratelimiter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.modak.notificationservice.service.impl.NotificationServiceImpl.Gateway;
import org.springframework.util.Assert;

/**
 * try to send a message according to the defined rules. see {@link NotificationRule}.
 * if rules set is empty, all type messages are allowed.
 */
public class RateLimiter {

  /**
   * The notificationRules, cannot be null.
   */
  List<NotificationRule> notificationRules;
  /**
   * The notificationSessions cannot be null.
   */
  Map<String, TypeMessageInfo> notificationSessions = new HashMap<>();

  /**
   * Default constructor
   */
  public RateLimiter() {
    this.notificationRules = new ArrayList<>();
  }

  /**
   * Constructor with mandatory params
   *
   * @param notificationRules cannot be null.
   */
  public RateLimiter(List<NotificationRule> notificationRules) {
    Assert.notNull(notificationRules, "The notificationRules cannot be null.");
    this.notificationRules = notificationRules;
  }

  /**
   * Send a message evaluating the {@link NotificationRule})
   * if any rules are not met, it throws an {@link NotificationException})
   *
   * @param type    cannot be null or empty.
   * @param userId  cannot be null or empty
   * @param message cannot be null or empty
   * @param gateway cannot be null.
   *
   * @throws NotificationException if there are too many requests for that user and type.
   */
  public void sendMessage(final String userId, final String type, final String message,
      final Gateway gateway) throws NotificationException {
    Assert.hasLength(type, "The type cannot be null or empty.");
    Assert.hasLength(userId, "The userId cannot be null or empty.");
    Assert.hasLength(message, "The message cannot be null or empty.");
    Assert.notNull(gateway, "The gateway cannot be null.");

    if (validateRequest(userId, type)) {
      gateway.send(userId, message);
    } else {
      throw new NotificationException(
          String.format("To many request of type: %s for the user: %s ", userId, type));
    }
  }

  /**
   * Return a boolean indicating if the user can send the type of message requested.
   *
   * @param type cannot be null or empty.
   * @param userId cannot be null or empty.
   *
   * @return true if the user can send message, otherwise false.
   */
  private boolean validateRequest(final String userId, final String type) {
    var notificationRule = getNotificationRule(type).orElse(null);
    if (Objects.isNull(notificationRule)) {
      return true;
    }
    String notificationKey = String.format("%s-%s", userId, type);

    if (!notificationSessions.containsKey(notificationKey)) {
      initializeKey(notificationKey);
      return true;
    }
    TypeMessageInfo typeMessageInfo = notificationSessions.get(notificationKey);

    LocalDateTime dateLimit = typeMessageInfo.getInitDate()
        .plus(1, notificationRule.getTimeUnit());

    if (LocalDateTime.now().isAfter(dateLimit)) {
      initializeKey(notificationKey);
      return true;
    } else {
      typeMessageInfo.plusCountMessage();
      return typeMessageInfo.getCountMessage() <= notificationRule.getMaxRequest();
    }
  }

  /**
   * Add Notification rule in the rule pool.
   *
   * @param notificationRule cannot be null.
   */
  public void addNotificationRule(final NotificationRule notificationRule) {
    Assert.notNull(notificationRule, "The notificationRule cannot be null.");
    notificationRules.add(notificationRule);
  }

  /**
   * Search the notificationRule in the rule set.
   * @param type cannot be null or empty.
   *
   * @return an {@link Optional} describing the search result for the rule in the rule set.
   */
  private Optional<NotificationRule> getNotificationRule(final String type) {
    Assert.hasLength(type, "The type cannot be null or empty.");
    return notificationRules.stream()
        .filter(x -> x.getTypeMessage().toString().equalsIgnoreCase(type)).findFirst();
  }

  /**
   * Create the initial value for the key if the message can be sent.
   * @param notificationKey cannot be null or empty.
   */
  private void initializeKey(final String notificationKey) {
    notificationSessions.put(notificationKey, new TypeMessageInfo());
  }
}
