package org.modak.notificationservice.ratelimiter;

import java.time.temporal.ChronoUnit;
import lombok.Data;
import org.springframework.util.Assert;

@Data
public class NotificationRule {

  private TypeMessage typeMessage;
  private long maxRequest;
  private ChronoUnit timeUnit;

  public NotificationRule(TypeMessage typeMessage, long maxRequest, ChronoUnit timeUnit) {
    Assert.notNull(typeMessage, "The typeMessage cannot be null.");
    Assert.isTrue(maxRequest > 0, "The maxRequest must greater that zero. ");
    Assert.notNull(timeUnit, "The timeUnit cannot be null.");

    this.typeMessage = typeMessage;
    this.maxRequest = maxRequest;
    this.timeUnit = timeUnit;
  }
}
