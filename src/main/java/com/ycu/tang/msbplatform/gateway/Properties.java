package com.ycu.tang.msbplatform.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Properties {
  // 工場ID
  @Value("${hadoop.pail.path}")
  private String pailPath;

  public String getPailPath() {
    return pailPath;
  }
}
