package com.ycu.tang.msbplatform.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Properties {
  @Value("${hadoop.namenode.url}")
  private String namenodeUrl;

  @Value("${hadoop.pail.path.root}")
  private String root;

  @Value("${hadoop.pail.path.data.root}")
  private String dataRoot;

  @Value("${hadoop.pail.path.outputs.root}")
  private String outputsRoot;

  @Value("${hadoop.pail.path.master.root}")
  private String masterRoot;

  @Value("${hadoop.pail.path.new.root}")
  private String newRoot;

  @Value("${kafka.url}")
  private String kafkaUrl;

  @Value("${kafka.topic.page_views}")
  private String kafkaTopicPageViews;

  @Value("${kafka.topic.person_property}")
  private String kafkaTopicPersonProperty;

  @Value("${kafka.topic.person_equiv}")
  private String kafkaTopicPersonEquiv;

  public String getNamenodeUrl() {
    return namenodeUrl;
  }

  public String getRoot() {
    return root;
  }

  public String getDataRoot() {
    return dataRoot;
  }

  public String getOutputsRoot() {
    return outputsRoot;
  }

  public String getMasterRoot() {
    return masterRoot;
  }

  public String getNewRoot() {
    return newRoot;
  }

  public String getKafkaUrl() {
    return kafkaUrl;
  }

  public String getKafkaTopicPageViews() {
    return kafkaTopicPageViews;
  }

  public String getKafkaTopicPersonProperty() {
    return kafkaTopicPersonProperty;
  }

  public String getKafkaTopicPersonEquiv() {
    return kafkaTopicPersonEquiv;
  }
}
