package org.modak.notificationservice.ratelimiter;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class TypeMessageInfo {

  private int countMessage;
  private final LocalDateTime initDate;

  public TypeMessageInfo() {
    this.countMessage = 1;
    this.initDate = LocalDateTime.now();
  }

  public void plusCountMessage(){
    countMessage ++;
  }
}
