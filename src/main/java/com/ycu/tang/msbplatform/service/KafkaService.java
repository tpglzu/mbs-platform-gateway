package com.ycu.tang.msbplatform.service;

import com.ycu.tang.msbplatform.gateway.GatewayController;
import com.ycu.tang.msbplatform.gateway.Properties;
import com.ycu.tang.msbplatform.gateway.thrift.Data;
import org.apache.kafka.clients.producer.*;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaService {
  Logger logger = LoggerFactory.getLogger(KafkaService.class);

  @Autowired
  protected Properties properties;

  protected Producer<String, String> producer = null;

  public void send(String topicName, List<Data> data) throws TException {

    TSerializer ser = new TSerializer();

    for (Data d : data) {
      getProduce().send(new ProducerRecord(topicName, ser.serialize(d)), new Callback() {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
          logger.info("kafka send completion. " + d);
          logger.error("kafka send exception. ", e);
        }
      });
    }

    getProduce().flush();
  }

  public void send(String topicName, Data data) throws TException {

    TSerializer ser = new TSerializer();
    getProduce().send(new ProducerRecord(topicName, ser.serialize(data)));
    getProduce().flush();
  }

  private Producer getProduce() {
    if (producer == null) {
      java.util.Properties props = new java.util.Properties();

      //Assign localhost id
      props.put("bootstrap.servers", properties.getKafkaUrl());

      //Set acknowledgements for producer requests.
      props.put("acks", "all");

      //If the request fails, the producer can automatically retry,
      props.put("retries", 0);

      //Specify buffer size in config
      props.put("batch.size", 16384);

      //Reduce the no of requests less than 0
      props.put("linger.ms", 1);

      //The buffer.memory controls the total amount of memory available to the producer for buffering.
      props.put("buffer.memory", 33554432);

      props.put("key.serializer",
              "org.apache.kafka.common.serialization.StringSerializer");

      props.put("value.serializer",
              "org.apache.kafka.common.serialization.ByteArraySerializer");

      producer = new KafkaProducer<String, String>(props);
    }

    return producer;
  }

  public void close() {
    if (producer == null) {
      return;
    } else {
      producer.close();
    }
  }
}
