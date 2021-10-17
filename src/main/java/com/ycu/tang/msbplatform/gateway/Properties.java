package com.ycu.tang.msbplatform.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Properties {
  @Value("${hadoop.pail.path}")
  private String pailPath;

  @Value("${hadoop.namenode.url}")
  private String namenodeUrl;

  public String getPailPath() {
    return pailPath;
  }

  public String getNamenodeUrl() {
    return namenodeUrl;
  }
}
