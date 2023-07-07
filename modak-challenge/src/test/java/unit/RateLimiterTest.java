package unit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modak.notificationservice.ratelimiter.NotificationException;
import org.modak.notificationservice.ratelimiter.NotificationRule;
import org.modak.notificationservice.ratelimiter.RateLimiter;
import org.modak.notificationservice.ratelimiter.TypeMessage;
import org.modak.notificationservice.service.impl.NotificationServiceImpl.Gateway;

public class RateLimiterTest {

  private final Gateway gateway = mock(Gateway.class);

  @Test
  void When_noRules_then_SendSuccessTest(){
    String userId = "123";
    String type = "NEW_OFFERS";
    String message = "loren ipsum";
    RateLimiter target = new RateLimiter();
    doNothing().when(gateway).send(userId, message);

    target.sendMessage(userId, type, message, gateway);

    verify(gateway, times(1)).send(userId, message);
  }

  @Test
  void When_thereAreRulesAndIsTheFirstMessage_then_SendSuccessTest(){
    String userId = "123";
    String type = TypeMessage.STATUS.name();
    String message = "loren ipsum";
    NotificationRule notificationRule = new NotificationRule(TypeMessage.STATUS, 2,
        ChronoUnit.HOURS);
    RateLimiter target = new RateLimiter(List.of(notificationRule));
    doNothing().when(gateway).send(userId, message);

    target.sendMessage(userId, type, message, gateway);

    verify(gateway, times(1)).send(userId, message);
  }

  @Test
  void When_passTheRule_then_SendSuccessTest(){
    String userId = "123";
    String type = TypeMessage.STATUS.name();
    String message = "loren ipsum";

    NotificationRule notificationRule = new NotificationRule(TypeMessage.STATUS, 2,
        ChronoUnit.HOURS);
    RateLimiter target = new RateLimiter(List.of(notificationRule));
    doNothing().when(gateway).send(userId, message);

    target.sendMessage(userId, type, message, gateway);
    target.sendMessage(userId, type, message, gateway);

    verify(gateway, times(2)).send(userId, message);
  }

  @Test
  void When_noPassTheRule_then_throwException(){
    String userId = "123";
    String type = TypeMessage.STATUS.name();
    String message = "loren ipsum";
    String expectedMessage = String.format("To many request of type: %s for the user: %s ",
        userId, type);

    NotificationRule notificationRule = new NotificationRule(TypeMessage.STATUS, 2,
        ChronoUnit.HOURS);
    RateLimiter target = new RateLimiter(List.of(notificationRule));
    doNothing().when(gateway).send(userId, message);

    target.sendMessage(userId, type, message, gateway);
    target.sendMessage(userId, type, message, gateway);
    NotificationException exception = assertThrows(NotificationException.class, () -> {
      target.sendMessage(userId, type, message, gateway);
    });

    verify(gateway, times(2)).send(userId, message);
    Assertions.assertNotNull(exception);
    Assertions.assertEquals(exception.getMessage(), expectedMessage);
  }
}
